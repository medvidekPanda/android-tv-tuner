package net.fogll.philips_control

import android.content.Context
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log
import android.view.Surface

class MySession(service: TvInputService) : TvInputService.Session(service) {
    override fun onSetSurface(surface: Surface?): Boolean {
        Log.d("PhilipsTest", "Surface set: $surface")
        return false
    }

    override fun onRelease() {
        Log.d("PhilipsTest", "Session released")
    }

    override fun onSetStreamVolume(volume: Float) {
        Log.d("PhilipsTest", "Stream volume set to: $volume")
    }

    override fun onTune(channelUri: Uri?): Boolean {
        Log.d("PhilipsTest", "Tuning to channel: $channelUri")
        notifyVideoAvailable()
        return false
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Log.d("PhilipsTest", "Captions enabled: $enabled")
    }

    fun getTunedFrequency(context: Context): Int? {
        Log.d("PhilipsTest", "Getting tuned frequency")

//        val tvInputManager = context.getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
//        val tvInputInfo = tvInputManager.tvInputList.find { it.id == currentChannelUri?.toString() }
//
//        return tvInputInfo?.let {
//            // Assuming the frequency is stored in the description or metadata
//            it.extras?.getInt("frequency")
//        }

        return null
    }
}