package net.fogll.philips_control

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.content.ContentValues
import android.content.Context
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.net.Uri

class EntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        Log.d("PhilipsTest", "inputId: ${findInputId(this)}")

        listChannels(this)
    }

    private fun addDvbChannel(
        channelName: String,
        frequency: Int,
        serviceId: Int,
        inputId: String
    ) {
        val contentResolver = this.contentResolver
        val channelUri: Uri = TvContract.Channels.CONTENT_URI

        val values = ContentValues().apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_DISPLAY_NAME, channelName)
            put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, frequency)
            put(TvContract.Channels.COLUMN_SERVICE_ID, serviceId)
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_DVB_T)
            put(TvContract.Channels.COLUMN_NETWORK_AFFILIATION, "Your Network")
            put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, 1)
            put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, 1)
            put(TvContract.Channels.COLUMN_SERVICE_ID, 1)
        }

        val newChannelUri = contentResolver.insert(channelUri, values)

        Log.d("PhilipsTest", "Channel added: $newChannelUri")
    }

    private fun listChannels(context: Context) {
        val contentResolver = context.contentResolver
        val channelsUri: Uri = TvContract.Channels.CONTENT_URI
        val projection = arrayOf(
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContract.Channels.COLUMN_INPUT_ID,
            TvContract.Channels.COLUMN_SERVICE_ID,
            // Add other columns you need here
        )

        val cursor = contentResolver.query(channelsUri, projection, null, null, null)

        cursor?.use {
            while (it.moveToNext()) {
                val channelName =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_DISPLAY_NAME))
                val inputId =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_INPUT_ID))
                val serviceId =
                    it.getInt(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_SERVICE_ID))
                // Retrieve other column values as needed

                Log.d(
                    "PhilipsTest",
                    "Channel: $channelName, Input ID: $inputId, Service ID: $serviceId"
                )
            }
        }
    }

    private fun findInputId(context: Context): String? {
        val tvInputManager = context.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        val tvInputs = tvInputManager?.tvInputList ?: return null

        for (input in tvInputs) {
            Log.d("PhilipsTest", "Input ID: ${input.id}")
            Log.d("PhilipsTest", "Type: ${input.type}")
            Log.d("PhilipsTest", "Parent ID: ${input.parentId}") // Might be useful for identifying built-in tuners

            if (input.type == TvInputInfo.TYPE_TUNER) {
                return input.id
            }
        }

        return null
    }
}


