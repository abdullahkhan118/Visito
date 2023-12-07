package com.horux.visito.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener

class FCMService : FirebaseMessagingService() {
    private var notificationCount = 0
    fun onNewToken(s: String) {
        super.onNewToken(s)
        token = s
        Log.e("FCM_Token", token)
    }

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        var messageBody = ""
        var title = ""
        if (remoteMessage.getNotification() != null) {
            val notification: RemoteMessage.Notification = remoteMessage.getNotification()
            if (notification.getBody() != null) messageBody = notification.getBody()
            if (notification.getTitle() != null) title = notification.getTitle()
            sendNotification(messageBody, title)
        }
    }

    private fun sendNotification(messageBody: String, title: String) {
        val intent: Intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL,
                "Visito",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(
            notificationCount /* ID of notification */,
            notificationBuilder.build()
        )
        notificationCount++
    }

    companion object {
        private const val NOTIFICATION_CHANNEL = "VISITO_NOTIFICATION"
        var token = ""
        fun setToken(): Task<String> {
            return FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(object : OnCompleteListener<String?>() {
                    fun onComplete(task: Task<String?>) {
                        if (task.isSuccessful()) token = task.getResult()
                        Log.e("FCM_Token", token)
                    }
                })
        }
    }
}
