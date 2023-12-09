package com.example.qrwificonnect.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.qrwificonnect.R
import com.example.qrwificonnect.databinding.FragmentMainBinding


class MainFragment : Fragment(R.layout.fragment_main) {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        val permissions = listOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.CHANGE_NETWORK_STATE)
        binding.continueButton.setOnClickListener {
            nextPage(permissions)
        }
        binding.qrButton?.setOnClickListener{
            nextPage(permissions)
        }
    }
    private fun nextPage(permissions:List<String>){
        var nextPage = false

        if(checkPermission(permissions[0])){
            if(checkPermission(permissions[1])){
                if (checkPermission(permissions[2])){
                    nextPage = true
                }
            }
        }
        if(nextPage){
            if(!isWifiEnabled()){
                Toast.makeText(requireContext(), "Turn On Wifi", Toast.LENGTH_SHORT).show()
            }
            else{
                findNavController().navigate(R.id.action_mainFragment_to_scannerFragment)
            }
        }
    }
    private fun isWifiEnabled(): Boolean {
        val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }
    private fun checkPermission(permissionType: String): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                permissionType
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //request camera permission
            requestPermissionLauncher.launch(permissionType)
            false
        } else {
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Release the binding when the fragment is destroyed
        _binding = null
    }
}