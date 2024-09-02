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

    override fun onTune(channelUri: Uri): Boolean {
        val frequency = channelUri.getQueryParameter("frequency")?.toIntOrNull()

        if (frequency != null) {
            Log.d("PhilipsTest", "Ladění na frekvenci: $frequency")
            notifyVideoAvailable()
            return true
        } else {
            Log.d("PhilipsTest", "Neplatná frekvence")
            notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN)
            return false
        }
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Log.d("PhilipsTest", "Captions enabled: $enabled")
    }
}