package com.example.qrwificonnect.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.qrwificonnect.R
import com.example.qrwificonnect.databinding.FragmentScannerBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.camera.CameraSettings

class ScannerFragment : Fragment(R.layout.fragment_scanner) {
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var barcodeView:DecoratedBarcodeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardViewContainer: CardView = binding.cameraSurface
        // Create DecoratedBarcodeView and add it to CardView
        barcodeView = DecoratedBarcodeView(requireContext())
        cardViewContainer.addView(barcodeView)
        val formats = listOf(BarcodeFormat.QR_CODE)
        // Apply settings to the DecoratedBarcodeView
        barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView.statusView.text = ""
        barcodeView.barcodeView.cameraSettings = CameraSettings().apply {
            isAutoFocusEnabled = true
        }

        barcodeView.resume()
        barcodeView.barcodeView.decodeSingle(callback)
        binding.continueButton.setOnClickListener {
                Toast.makeText(requireContext(), "Scan a QR Code to connect",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_scannerFragment_to_mainFragment)
        }
    }

    private val callback = BarcodeCallback { result ->
        if (result != null) {
            val resultText = result.text
            if(resultText.startsWith("WIFI:")){
                val parsedResult = wifiCodeParser(resultText.slice(5..<resultText.length))
                var ssid = ""
                var password = ""
                for (i in parsedResult){
                    when{
                        i.startsWith("P:") -> password = i.slice(2..<i.length)
                        i.startsWith("S:") -> ssid = i.slice(2..<i.length)
                        else -> println(i)
                    }
                }
                val action = ScannerFragmentDirections.actionScannerFragmentToConnectedFragment(ssid, password)
                barcodeView.barcodeView.cameraSettings.isAutoFocusEnabled = false
                barcodeView.pause()
                findNavController().navigate(action)

            }
            else{
                Toast.makeText(requireContext(), "Invalid Qr Code", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_scannerFragment_to_mainFragment)
            }
        }
    }

    private fun wifiCodeParser(content: String):MutableList<String> {
        var newcontent = content
        if(!content.endsWith(";;")){
            newcontent = "$newcontent;"
        }
        val decodedList = mutableListOf<String>()
        var temp = ""
        var counter = 0
        val lengths = content.length


        while (counter < lengths) {
            if ((newcontent[counter] == '\\') and ((newcontent[counter + 1] == ';') or (newcontent[counter + 1] == ':'))) {
                temp += content[counter + 1]
                counter += 2
                continue
            }
            if (newcontent[counter] == ';') {
                decodedList.add(temp)
                counter += 1
                temp = ""

            }
            temp += newcontent[counter]

            counter += 1
        }
        return decodedList
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Release the binding when the fragment is destroyed
        _binding = null
    }
}
