package com.example.moengagenewsapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.moengagenewsapp.MainActivity
import com.example.moengagenewsapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"
const val notificationId = "0"
const val channelName = "com.example.moengagenewsapp"

class MyFirebaseMessagingService :FirebaseMessagingService() {


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(message: RemoteMessage) {
        if(message != null){
            generateNotification(message.notification!!.title!!, message.notification!!.body!!)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun generateNotification(title :String, message:String){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            channelId).setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())
    }

    fun getRemoteView(title: String, message: String ): RemoteViews? {
        val remoteViews = RemoteViews("com.example.firebasepushnotification",R.layout.notification)

        remoteViews.setTextViewText(R.id.id_text,title)
        remoteViews.setImageViewResource(R.id.id_logo,R.drawable.baseline_circle_notifications_24)
        return remoteViews
    }


}