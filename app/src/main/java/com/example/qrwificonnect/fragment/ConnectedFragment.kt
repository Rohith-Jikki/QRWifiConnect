package com.example.qrwificonnect.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
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
import com.example.qrwificonnect.ApiInterface
import com.example.qrwificonnect.Device
import com.example.qrwificonnect.R
import com.example.qrwificonnect.RetrofitClient
import com.example.qrwificonnect.databinding.FragmentConnectedBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


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
        binding.ssidValue.setText( args.argSsid)
        binding.passValue.setText(args.argPassword)
        val apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface::class.java)
        binding.onBtn.setOnClickListener {
            sendApiRequestOn(deviceName = binding.deviceNameText.text.toString())
        }
        binding.offBtn.setOnClickListener {
            sendApiRequestOff(deviceName = binding.deviceNameText.text.toString())
        }
    }

    private fun sendApiRequestOn(deviceName:String){
        val apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface::class.java)
        val call: Call<Device> = apiInterface.turnOn(deviceName, args.argSsid, args.argPassword)
        call.enqueue(object: Callback<Device>{
            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if(response.body() != null) {
                    Toast.makeText(requireContext(), "Received", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(), "Received empty", Toast.LENGTH_SHORT).show()
                    Log.d("rawResponse",response.raw().toString())
                }
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                if(t is IOException){
                    Toast.makeText(requireContext(), "Network Problem", Toast.LENGTH_SHORT).show()
                    Log.d("Error_TAG", "onFailure: Error: " + t.message)
                }
                else{
                    Toast.makeText(requireContext(), "Big Problem", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun sendApiRequestOff(deviceName:String){
        val apiInterface = RetrofitClient.getRetrofitInstance().create(ApiInterface::class.java)
        val call: Call<Device> = apiInterface.turnOff(deviceName, args.argSsid, args.argPassword)
        call.enqueue(object: Callback<Device>{
            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if(response.body() != null) {
                    Toast.makeText(requireContext(), "Received", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(requireContext(), "Received empty", Toast.LENGTH_SHORT).show()
                    Log.d("rawResponse",response.raw().toString())
                }
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                if(t is IOException){
                    Toast.makeText(requireContext(), "Network Problem", Toast.LENGTH_SHORT).show()
                    Log.d("Error_TAG", "onFailure: Error: " + t.message)
                }
                else{
                    Toast.makeText(requireContext(), "Big Problem", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectToWifi(ssid:String, password:String){
        val wifiManager = requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val suggestion = WifiNetworkSuggestion.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .setIsAppInteractionRequired(true)
            .build()
//        val specifier: NetworkSpecifier = WifiNetworkSpecifier.Builder()
//            .setSsid(ssid)
//            .setWpa2Passphrase(password)
//            .build()
//        val request: NetworkRequest = NetworkRequest.Builder()
//            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            .setNetworkSpecifier(specifier)
//            .build()
        wifiManager.removeNetworkSuggestions(listOf(suggestion))
        wifiManager.addNetworkSuggestions(listOf(suggestion))
        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return
                }
            }
        }
        requireActivity().applicationContext.registerReceiver(broadcastReceiver, intentFilter)


//        connectManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        connectManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
//            override fun onAvailable(network: android.net.Network) {
//                super.onAvailable(network)
//                Log.d("WifiConnect", "Connected to Wi-Fi network")
//                connectManager.bindProcessToNetwork(network)
//
//                // Perform actions when connected
//            }
//            override fun onUnavailable(){
//                super.onUnavailable()
//                Log.d("WifiConnect", "Unable to connect to Wi-Fi network")
//                // Handle connection failure
//            }
//        })

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