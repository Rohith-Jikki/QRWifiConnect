package com.example.qrwificonnect.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.qrwificonnect.R
import com.example.qrwificonnect.databinding.FragmentConnectedBinding


class ConnectedFragment : Fragment(R.layout.fragment_connected) {

    private var _binding: FragmentConnectedBinding? = null
    private val binding get() = _binding!!
    private val args: ConnectedFragmentArgs by navArgs()
    private lateinit var connectManager: ConnectivityManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentConnectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                connectToWifi( ssid = args.argSsid, password = args.argPassword)

            }
            Build.VERSION.SDK_INT in Build.VERSION_CODES.M until Build.VERSION_CODES.Q -> {
                connectToWifiLower(ssid = args.argSsid, password = args.argPassword)
            }
            else -> {
                findNavController().navigate(R.id.action_connectedFragment_to_mainFragment)
                Toast.makeText(requireContext(), "Couldn't Connect", Toast.LENGTH_SHORT).show()
            }

        }
        binding.continueButton.setOnClickListener {
                Toast.makeText(requireContext(), "Connected", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_connectedFragment_to_mainFragment)
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWifi(ssid:String, password:String){
        val specifier: NetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()
        val request: NetworkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build()
        connectManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                super.onAvailable(network)
                Log.d("WifiConnect", "Connected to Wi-Fi network")

                // Perform actions when connected
            }
            override fun onUnavailable(){
                super.onUnavailable()
                Log.d("WifiConnect", "Unable to connect to Wi-Fi network")
                // Handle connection failure
            }
        })

    }

private fun connectToWifiLower(ssid:String, password: String){
    val wifiManager = requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if(!wifiManager.isWifiEnabled){
        wifiManager.setWifiEnabled(true)
    }
    val conf = WifiConfiguration()
    conf.SSID = "\"" + ssid + "\""
    conf.preSharedKey = "\"" + password + "\""

    val networkId = wifiManager.addNetwork(conf)
    wifiManager.enableNetwork(networkId, true)
    wifiManager.reconnect()


}

    override fun onDestroyView() {
        super.onDestroyView()
        // Release the binding when the fragment is destroyed
        _binding = null
    }
}