package com.example.moengagenewsapp.networking

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class NetworkCheck(val connectivityManager: ConnectivityManager) {

    @RequiresApi(Build.VERSION_CODES.M)
    fun performAction(action : () -> Unit){
        if(hasValidInternetConn()){
            action()

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
     fun hasValidInternetConn() :Boolean{
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)?:return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    }
}
