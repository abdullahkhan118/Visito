package com.horux.visito

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.horux.visito.services.FCMService

class VisitoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().subscribeToTopic("android")
        FCMService.setToken()
    }

}