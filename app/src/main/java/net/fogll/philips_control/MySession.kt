package net.fogll.philips_control

import android.content.Context
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log
import android.view.Surface

class MySession(context: Context) : TvInputService.Session(context) {
    private var currentChannelUri: Uri? = null

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

    fun getTunedFrequency(context: Context, input: String): Int? {
        Log.d("PhilipsTest", "Getting tuned frequency")

        val tvInputManager = context.getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
        val tvInputInfo = tvInputManager.tvInputList.find { it.id == input }

        if (tvInputInfo != null) {
            // Assuming there's a method or property in TvInputInfo to get the frequency
            // This is a placeholder as the actual method/property might differ
            val frequency = tvInputInfo.extras?.getInt("frequency")
            Log.d("PhilipsTest", "Tuned frequency: $frequency")
            return frequency
        }

        Log.d("PhilipsTest", "TV Input Info not found for input: $input")
        return null
    }
}