package net.fogll.philips_control

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.tv.TvContract
import android.media.tv.TvInputManager
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
            Log.d("PhilipsTest", "Ladění na frekvenci: $frequency")

            val channels = getChannelsOnFrequency(frequency)
            if (channels.isNotEmpty()) {
                Log.d("PhilipsTest", "Kanály nalezeny na frekvenci: $frequency")
                Log.d("PhilipsTest", "Detaily kanálů: $channels")
            } else {
                Log.d("PhilipsTest", "Žádné kanály na frekvenci: $frequency")
            }

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

    @SuppressLint("Range")
    private fun getChannelsOnFrequency(frequency: Int): List<String> {
        val channels = mutableListOf<String>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri: Uri = TvContract.buildChannelsUriForInput("input_id") // Replace "input_id" with actual input ID

        val projection = arrayOf(TvContract.Channels.COLUMN_DISPLAY_NAME)
        val selection = "${TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID} = ?"
        val selectionArgs = arrayOf(frequency.toString())

        val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            while (it.moveToNext()) {
                val channelName = it.getString(it.getColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NAME))
                channels.add(channelName)
            }
        }

        return channels
    }
}