package net.fogll.philips_control

import android.content.Context
import android.media.tv.TvContract
import android.net.Uri
import android.util.Log
import android.view.Surface
import android.media.tv.TvInputService.Session
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource

class MySession(private val context: Context) : Session(context) {
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onTune(channelUri: Uri?): Boolean {
        Log.d("PhilipsTest", "Tuning to: $channelUri")

//        if (channelUri == null) {
//            Log.e("PhilipsTest", "Channel URI is null")
//            return false
//        }
//
//        // 1. Create a DataSource.Factory
//        //val httpDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSource.Factory()
//        //    .setUserAgent("YourUserAgent")
//
//        // 2. Build the DefaultDataSourceFactory
//        //val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)
//
//        // 3. Create a MediaSource
////        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
////            .createMediaSource(MediaItem.fromUri("http://10.40.196.82:8080/bysid/402"))
//
//        // 1. Create a DataSource.Factory
//        val contentDataSourceFactory = DefaultDataSource.Factory(context)
//
//        // 2. Create a MediaSource
//        val mediaSource = ProgressiveMediaSource.Factory(contentDataSourceFactory)
//            .createMediaSource(MediaItem.fromUri(channelUri))
//
//        // 3. Create and configure ExoPlayer
//        player = ExoPlayer.Builder(context).build()
//        player?.setMediaSource(mediaSource)

        return true
    }

    override fun onSetSurface(surface: Surface?): Boolean {
        Log.d("PhilipsTest", "Surface set: $surface")
//        if (surface != null) {
//            player?.setVideoSurface(surface)
//
//            // Prepare and play after surface is set
//            player?.prepare()
//            player?.play()
//
//            notifyVideoAvailable()
//            return true
//        } else {
//            player?.clearVideoSurface()
//            return false
//        }

        return true
    }

    override fun onRelease() {
        Log.d("PhilipsTest", "Session released")
        player?.release()
        player = null
    }

    override fun onSetStreamVolume(volume: Float) {
        Log.d("PhilipsTest", "Stream volume set to: $volume")
        //player?.volume = volume
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Log.d("PhilipsTest", "Caption enabled: $enabled")
        // Handle caption settings for ExoPlayer if needed
    }
}