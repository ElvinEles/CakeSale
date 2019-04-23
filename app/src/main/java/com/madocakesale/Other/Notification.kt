package com.madocakesale.Other

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class Notification: Application() {

    companion object{
        var CHANNEL_ID:String="CHANNEl 1"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channel1:NotificationChannel= NotificationChannel(CHANNEL_ID,"Channel 1",NotificationManager.IMPORTANCE_HIGH)
            channel1.description="This is channel 1"

            var manager:NotificationManager=getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel1)
        }
    }
}