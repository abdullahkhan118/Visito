package com.horux.visito.network_check

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager

object InternetCheck {
    //    var networkCapabilities: List<NetworkCapabilities?> = listOf(
    ////        val hashMap = hashMapOf<String, Boolean>("wifi" to false, "cellular" to false))
    var STRING_WIFI = "wifi"
    var STRING_CELLULAR = "cellular"
    var networkCapabilities: MutableList<NetworkCapabilities?> = ArrayList()

    //    fun internetAvailable(context: Context): Boolean {
    //        val networks: HashMap<String, Boolean> = checkConnectivity(context)
    //        networks.keys.forEach {
    //            if (networks[it] == true) {
    //                showToast(context, "Internet Available")
    //                return true
    //            }
    //        }
    //        showToast(context, "No Internet")
    //        return false
    //    }
    fun internetAvailable(context: Context): Boolean {
        val networks = checkConnectivity(context)
        for (key in networks.keys) {
            if (networks[key]!!) return true
        }
        return false
    }

    //    fun checkConnectivity(context: Context): HashMap<String, Boolean> {
    //        val hashMap = hashMapOf<String, Boolean>("wifi" to false, "cellular" to false)
    //
    //        val connectivityManager: ConnectivityManager = context.applicationContext
    //                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //
    //        networkCapabilities = connectivityManager.allNetworks.map {
    //            return@map connectivityManager.getNetworkCapabilities(it)
    //        }
    //        if (_isWifiAvailable(context.applicationContext, networkCapabilities)) {
    //            showToast(context, "Wifi is Available")
    //            hashMap["wifi"] = true
    //        } else showToast(context, "No Wifi")
    //        if (_isMobileDataAvailable(networkCapabilities)) {
    //            showToast(context, "Mobile Data is Available")
    //            hashMap["cellular"] = true
    //        } else showToast(context, "No Mobile Data")
    //        return hashMap
    //    }
    fun checkConnectivity(context: Context): HashMap<String, Boolean> {
        val hashMap = HashMap<String, Boolean>()
        hashMap[STRING_WIFI] = false
        hashMap[STRING_CELLULAR] = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCapabilities = ArrayList()
        for (network in connectivityManager.allNetworks) {
            networkCapabilities.add(connectivityManager.getNetworkCapabilities(network))
        }
        if (_isWifiAvailable(context, networkCapabilities)) {
            hashMap[STRING_WIFI] = true
        }
        if (_isMobileDataAvailable(networkCapabilities)) {
            hashMap[STRING_CELLULAR] = true
        }
        return hashMap
    }

    //
    //    private fun _isWifiAvailable(
    //            context: Context,
    //            capabilities: List<NetworkCapabilities?>
    //    ): Boolean {
    //        return isWifiEnabled(context = context)
    //                && capabilities.filterNotNull().any {
    //            (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
    //                    it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE))
    //        }
    //    }
    private fun _isWifiAvailable(
        context: Context,
        capabilities: List<NetworkCapabilities?>
    ): Boolean {
        var hasTransport = false
        for (capability in capabilities) {
            if (capability!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capability.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI_AWARE
                )
            ) {
                hasTransport = true
                break
            }
        }
        return isWifiEnabled(context) && hasTransport
    }

    //    private fun isWifiEnabled(context: Context): Boolean {
    //        val connectivityManager: ConnectivityManager = context.applicationContext
    //                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    //
    //        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
    //                && (context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager).isWifiEnabled
    //    }
    private fun isWifiEnabled(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return (context.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)
                && wifiManager.isWifiEnabled)
    }

    //
    //    private fun _isMobileDataAvailable(capabilities: List<NetworkCapabilities?>): Boolean {
    //        return capabilities.any {
    //            return@any it?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
    //        }
    //    }
    private fun _isMobileDataAvailable(capabilities: List<NetworkCapabilities?>): Boolean {
        for (capability in capabilities) {
            val hasTransport = capability!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            if (hasTransport) return true
        }
        return false
    }
}
