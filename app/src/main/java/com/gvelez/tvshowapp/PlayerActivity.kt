package com.gvelez.tvshowapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.gvelez.tvshowapp.databinding.ActivityPlayerBinding
import com.gvelez.tvshowapp.video.VideoService
import com.gvelez.tvshowapp.video.VideoService.Companion.STREAM_URL
import dagger.hilt.android.AndroidEntryPoint
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val youtubeLink = "https://www.youtube.com/watch?v=1QxtWu-pJw0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
//        extractWithYoutubeExtractor(youtubeLink)
//        convertLinkToHls(youtubeLink)
//FIXME this url should come from Vimeo WS requested via VideoRepository.kt
        playVideo("https://155vod-adaptive.akamaized.net/exp=1623813089~acl=%2F117491754%2F%2A~hmac=946ff359be5a7c48d05969c69b897d09153150f84162ab5ed718a6cb1b27cf29/117491754/sep/video/328558768,328558759/master.m3u8")
    }

    private fun playVideo(downloadUrl: String) {
        //Start the service
        val intent = Intent(this, VideoService::class.java).apply {
            putExtra(STREAM_URL, downloadUrl)
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Create our connection to the service to be used in our bindService call.
     */
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}

        /**
         * Called after a successful bind with our PlayerService.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is VideoService.PlayerServiceBinder) {
                binding.playerView.player =
                    service.getPlayerHolderInstance().audioFocusPlayer
            }
        }
    }

    private fun extractWithYoutubeExtractor(downloadUrl: String) {
        object : YouTubeExtractor(this) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                println(ytFiles)

                if (ytFiles != null) {
                    val itag = 18 //37 -> 1920x1080 - 22 -> 1280x720 or 18
                    val downloadUrl = ytFiles.get(itag).url
                    playVideo(downloadUrl)
                }
            }
        }.extract(downloadUrl, true, true)
    }


    fun convertLinkToHls(youtubeLiveLink: String) {
        val hlsLink = arrayOf("")
        val runnableCode = Runnable {
            try {
                val url = URL(youtubeLiveLink)
                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                val instream = BufferedReader(
                    InputStreamReader(con.inputStream)
                )
                var inputLine: String?
                val content = StringBuffer()
                while (instream.readLine().also { inputLine = it } != null) {
                    content.append(inputLine)
                }

                Log.e("INSTREAM", content.toString())

                instream.close()
                if (youtubeLiveLink.contains("m3u8")) {
                    hlsLink[0] = youtubeLiveLink
                } else {
                    hlsLink[0] = extractHlsUrl(content.toString()).toString()
                }
                con.disconnect()
            } catch (e: Exception) {
                hlsLink[0] = youtubeLiveLink
                e.printStackTrace()
                Log.d(
                    "TAG", "convertLinkToHls: " +
                            ": Reason is that the link is not a live url," +
                            "copy the link by open video then right click, copy video url from youtube."
                )
            }
        }
        val requestThread = Thread(runnableCode)
        requestThread.start()
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (!requestThread.isAlive) {
                    // hlsLink[0]
                    runOnUiThread {
                        Log.e("HLSPLAY", hlsLink[0])
                        playVideo(hlsLink[0])
                    }
                    timer.cancel()

                    /* Here initialize the player,
                     * make you do it in runOnUiTHread() */
                }
            }
        }, 1000, 1000)
    }

    private fun extractHlsUrl(response: String): String? {
        val keyName = "hlsManifestUrl"
        if (response.contains(keyName)) {
            var index = response.indexOf(keyName)
            index += 17
            var lastIndex = index
            while (lastIndex < response.length) {
                if (response[lastIndex] == '8' &&
                    response[lastIndex - 1] == 'u' &&
                    response[lastIndex - 2] == '3' &&
                    response[lastIndex - 3] == 'm'
                ) {
                    break
                }
                lastIndex++
            }
            return response.substring(index, lastIndex + 1)
        }
        return null
    }
}
