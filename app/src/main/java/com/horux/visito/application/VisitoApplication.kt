package com.horux.visito.application

import com.facebook.FacebookSdk
class VisitoApplication : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
        val info: android.content.pm.PackageInfo
        try {
            info = packageManager.getPackageInfo("com.horux.visito", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: java.security.MessageDigest
                md = java.security.MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(android.util.Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                android.util.Log.e("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            android.util.Log.e("name not found", e1.toString())
        } catch (e: java.security.NoSuchAlgorithmException) {
            android.util.Log.e("no such an algorithm", e.toString())
        } catch (e: java.lang.Exception) {
            android.util.Log.e("exception", e.toString())
        }
    }
}
