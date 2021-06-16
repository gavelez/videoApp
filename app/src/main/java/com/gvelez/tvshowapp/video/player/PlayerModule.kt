package com.gvelez.tvshowapp.video.player

import android.content.Context
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.gvelez.tvshowapp.R
import com.gvelez.tvshowapp.video.DescriptionAdapter
import com.gvelez.tvshowapp.video.VideoService

/**
 * Just a simple injection object, builds stuff.
 */
object PlayerModule {
    fun getPlayerHolder(context: Context, streamUrl: String) = PlayerHolder(context, streamUrl, PlayerState())

    fun getPlayerNotificationManager(context: Context): PlayerNotificationManager =
        PlayerNotificationManager.createWithNotificationChannel(
            context,
            VideoService.NOTIFICATION_CHANNEL,
            R.string.app_name,
            VideoService.NOTIFICATION_ID,
            getDescriptionAdapter(context)
        ).apply {
            setFastForwardIncrementMs(0)
//            setOngoing(true)
            setUseNavigationActions(false)
            setRewindIncrementMs(0)
//            setStopAction(null)
        }

    private fun getDescriptionAdapter(context: Context) = DescriptionAdapter(context)
}