package net.fogll.philips_control

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri


class EntryActivity : AppCompatActivity() {
    private var myTvInputService: MyTvInputService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

//        addDvbChannel(
//            "CT 2 test",
//            274000000,
//            102,
////            "com.mediatek.tvinput/.dtv.TunerInputService/HW0",
//            "dummy_input",
//        )

        //startService()
        contentResolver.notifyChange(TvContract.Channels.CONTENT_URI, null)
        listChannels(this, "dummy_input")

//        val channelId = ContentUris.parseId("content://android.media.tv/channel/1".toUri())
//        Log.d("PhilipsTest", "Channel ID: $channelId")
//
//        val channelUri = TvContract.buildChannelUri(channelId)
//        val intent = Intent(Intent.ACTION_VIEW, channelUri)
//        intent.setPackage("com.android.tv")
//        startActivity(intent)
    }


    private fun addDvbChannel(
        channelName: String,
        frequency: Int,
        serviceId: Int,
        inputId: String,
    ) {


        val values = ContentValues().apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_DISPLAY_NAME, channelName)
            put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, frequency)
            put(TvContract.Channels.COLUMN_SERVICE_ID, serviceId)
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_DVB_C)
            put(TvContract.Channels.COLUMN_NETWORK_AFFILIATION, "SATT")
            put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, 1)
            put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, serviceId)
            put(TvContract.Channels.COLUMN_VIDEO_FORMAT, TvContract.Channels.VIDEO_FORMAT_1080I)
            put(TvContract.Channels.COLUMN_DESCRIPTION, "Channel Description")
            //put(TvContract.Channels.COLUMN_APP_LINK_INTENT_URI, createAppLinkIntentUri(context))
        }

        val newChannelUri = contentResolver.insert(TvContract.Channels.CONTENT_URI, values)
        //contentResolver.notifyChange(TvContract.Channels.CONTENT_URI, null)
        contentResolver.notifyChange(newChannelUri!!, null)
        Log.d("PhilipsTest", "Channel added: $newChannelUri")
    }

    private fun listChannels(context: Context, inputId: String) {
        val contentResolver = context.contentResolver
        //val channelsUri: Uri = TvContract.Channels.CONTENT_URI
        val channelsUri: Uri =
            TvContract.buildChannelsUriForInput(
                inputId
            )

        val projection = arrayOf(
            TvContract.Channels._ID,
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContract.Channels.COLUMN_INPUT_ID,
            TvContract.Channels.COLUMN_SERVICE_ID,
            TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID,
            TvContract.Channels.COLUMN_TYPE,
        )

        Log.d("PhilipsTest", "channelsUri: $channelsUri")

        val cursor = contentResolver.query(channelsUri, projection, null, null, null)
        val channelList = StringBuilder()

        cursor?.use {
            while (it.moveToNext()) {
                val channelName =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_DISPLAY_NAME))
                val inputId =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_INPUT_ID))
                val serviceId =
                    it.getInt(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_SERVICE_ID))
                val tsId =
                    it.getInt(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID))
                val type =
                    it.getInt(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_TYPE))

                Log.d(
                    "PhilipsTest",
                    "Channel: $channelName, Input ID: $inputId, Service ID: $serviceId, $tsId, $type"
                )

                val channelId = it.getLong(it.getColumnIndexOrThrow(TvContract.Channels._ID))
                val singleChannelUri = TvContract.buildChannelUri(channelId)
                Log.d("PhilipsTest", "singleChannelUri: $singleChannelUri")
                channelList.append("Channel: $channelName, Input ID: $inputId, Service ID: $serviceId, TS ID: $tsId, Type: $type\n")
            }
        }

        val channelListTextView: TextView = findViewById(R.id.channel_list)
        val result = if (channelList.isEmpty()) {
            "No channels found"
        } else {
            channelList.toString()
        }

        channelListTextView.text = result
    }

//    private fun startService(surface: Surface) {
//        Log.d("PhilipsTest", "Permission granted. Start service")
//        val intent = Intent(this, MyTvInputService::class.java)
//        intent.putExtra("surface", surface)
//        startService(intent)
//    }

    private fun startService() {
        Log.d("PhilipsTest", "Permission granted. Start service")
        val intent = Intent(this, MyTvInputService::class.java)
        startService(intent)
    }

    private fun findInputId(context: Context): String? {
        val tvInputManager = context.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        val tvInputs = tvInputManager?.tvInputList ?: return null

        for (input in tvInputs) {
            Log.d("PhilipsTest", "Input ID: ${input.id}")
            Log.d("PhilipsTest", "Type: ${input.type}")
            Log.d(
                "PhilipsTest",
                "Parent ID: ${input.parentId}"
            ) // Might be useful for identifying built-in tuners

            if (input.type == TvInputInfo.TYPE_TUNER) {
                //   return input.id
            }
        }

        return null
    }

    private fun createAppLinkIntentUri(context: Context): String {
        val intent = Intent(context, EntryActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(intent)
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        return pendingIntent?.intentSender?.toString() ?: ""
    }
}


