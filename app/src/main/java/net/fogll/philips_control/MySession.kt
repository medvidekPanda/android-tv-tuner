package net.fogll.philips_control

import android.content.Context
import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log
import android.view.Surface


class MySession(private val context: Context) : TvInputService.Session(context) {
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

    override fun onTune(channelUri: Uri): Boolean {
        val frequency = channelUri.getQueryParameter("frequency")?.toIntOrNull()
        if (frequency != null) {
            // Tune to the specified frequency (implementation depends on tuner access)
            tuneToFrequency(frequency)
            notifyVideoAvailable()
            return true
        }

        return false
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Log.d("PhilipsTest", "Captions enabled: $enabled")
    }

    private fun tuneToFrequency(frequency: Int) {
        Log.d("PhilipsTest", "Tuning to frequency 2: $frequency")
    }
}