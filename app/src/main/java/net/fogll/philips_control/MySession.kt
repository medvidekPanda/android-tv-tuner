package net.fogll.philips_control

import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log
import android.view.Surface

class MySession(service: TvInputService) : TvInputService.Session(service) {
    override fun onSetSurface(surface: Surface?): Boolean {
        Log.d("TVInput", "Surface set: $surface")
        return false
    }

    override fun onRelease() {
        Log.d("TVInput", "Session released")
    }

    override fun onSetStreamVolume(volume: Float) {
        Log.d("TVInput", "Stream volume set to: $volume")
    }

    override fun onTune(channelUri: Uri?): Boolean {
        Log.d("TVInput", "Tuning to channel: $channelUri")
        notifyVideoAvailable()
        return false
    }

    override fun onSetCaptionEnabled(enabled: Boolean) {
        Log.d("TVInput", "Captions enabled: $enabled")
    }
}