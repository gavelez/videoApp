package com.gvelez.tvshowapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LaunchPlayerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notifyIntent = Intent(context, PlayerActivity::class.java)
        context.startActivity(notifyIntent)
    }
}