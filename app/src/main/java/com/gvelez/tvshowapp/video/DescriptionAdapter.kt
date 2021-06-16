package com.gvelez.tvshowapp.video

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.gvelez.tvshowapp.R

class DescriptionAdapter(private val context: Context) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): String =
        context.getString(R.string.notification_player_channel_name)

    override fun getCurrentContentText(player: Player): String? = null

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? = null

    /**
     * Specify the PendingIntent which is fired whenever there's a click on the notification.
     * This pending intent will fire a broadcast, which will be captured by a BroadcastReceiver
     * we have to put in the app module.
     * The broadcast receiver will intercept this intent and start the player activity.
     *
     * Since I want this component to be decoupled from the PlayerActivity implementation
     * here I have to do a workaround, using polling and forging an explicit Intent (as I do not know which
     * is the name of the player activity I would have to start) because of the security
     * limitations introduced on O+.
     *
     * https://commonsware.com/blog/2017/04/11/android-o-implicit-broadcast-ban.html
     * */
    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val intent = Intent().apply {
            action = "com.gvelez.tvshowapp.LAUNCH_PLAYER_ACTIVITY"
        }
        val matches = context.packageManager.queryBroadcastReceivers(intent, 0)

        val explicit = Intent(intent)
        for (resolveInfo in matches) {
            val componentName = ComponentName(
                resolveInfo.activityInfo.applicationInfo.packageName,
                resolveInfo.activityInfo.name
            )

            explicit.component = componentName
        }

        return PendingIntent.getBroadcast(context, 0, explicit, 0)
    }
}