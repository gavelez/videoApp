package com.gvelez.tvshowapp.video.player

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import androidx.media.AudioAttributesCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import java.util.*

/**
 * Creates and manages a [com.google.android.exoplayer2.ExoPlayer] instance.
 */
class PlayerHolder(
    context: Context,
    private val streamUrl: String,
    private val playerState: PlayerState
) {
    val audioFocusPlayer: ExoPlayer

    // Create the player instance.
    init {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributesCompat.Builder()
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .build()
        audioFocusPlayer = AudioFocusWrapper(
            audioAttributes,
            audioManager,
            ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        ).apply {
            prepare(buildMediaSource(Uri.parse(streamUrl)))
        }
        //FIXME WARN "SimpleExoPlayer created"
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        lateinit var videoSource: MediaSource

        return if (uri.toString().uppercase(Locale.getDefault()).contains("M3U8")) {
            HlsMediaSource.Factory(DefaultHttpDataSourceFactory("exo-radiouci")).createMediaSource(uri)
        } else {
            ExtractorMediaSource(
                uri, DefaultHttpDataSourceFactory("exo-radiouci"), DefaultExtractorsFactory(),
                null, null
            )
        }
    }

    // Prepare playback.
    fun start() {
        with(audioFocusPlayer) {
            // Restore state (after onResume()/onStart())
            prepare(buildMediaSource(Uri.parse(streamUrl)))
            with(playerState) {
                // Start playback when media has buffered enough
                // (whenReady is true by default).
                playWhenReady = whenReady
                seekTo(window, position)
                // Add logging.
                attachLogging(audioFocusPlayer)
            }
            //FIXME WARN "SimpleExoPlayer is started"
        }
    }

    // Stop playback and release resources, but re-use the player instance.
    fun stop() {
        with(audioFocusPlayer) {
            // Save state
            with(playerState) {
                position = currentPosition
                window = currentWindowIndex
                whenReady = playWhenReady
            }
            // Stop the player (and release it's resources). The player instance can be reused.
            stop(true)
        }
        //FIXME WARN "SimpleExoPlayer is stopped"
    }

    // Destroy the player instance.
    fun release() {
        audioFocusPlayer.release() // player instance can't be used again.
        //FIXME INFO  "SimpleExoPlayer is released"
    }

    /**
     * For more info on ExoPlayer logging, please review this
     * [codelab](https://codelabs.developers.google.com/codelabs/exoplayer-intro/#5).
     */
    private fun attachLogging(exoPlayer: ExoPlayer) {
        // Write to log on state changes.
        exoPlayer.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                //FIXME INFO "playerStateChanged: ${getStateString(playbackState)}, $playWhenReady"
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                //FIXME INFO "playerError: $error"
            }

            fun getStateString(state: Int): String {
                return when (state) {
                    Player.STATE_BUFFERING -> "STATE_BUFFERING"
                    Player.STATE_ENDED -> "STATE_ENDED"
                    Player.STATE_IDLE -> "STATE_IDLE"
                    Player.STATE_READY -> "STATE_READY"
                    else -> "?"
                }
            }
        })
    }
}