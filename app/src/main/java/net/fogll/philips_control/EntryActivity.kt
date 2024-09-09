package net.fogll.philips_control

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ComponentName
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.tvprovider.media.tv.Channel
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat

class EntryActivity : AppCompatActivity() {
    private var myTvInputService: MyTvInputService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

//        addDvbChannel(
//            "CT 2",
//            274000000,
//            101,
//            "com.mediatek.tvinput/.dtv.TunerInputService/HW0",
//            1,
//            this
//        )

        //addDvbChannel2("CT 1")
        //addSurfaceView()
        listChannels(this)

        val channelId = ContentUris.parseId("content://android.media.tv/channel/24".toUri())

        Log.d("PhilipsTest", "Channel ID: $channelId")

        val channelUri = TvContract.buildChannelUri(channelId)
        val intent = Intent(Intent.ACTION_VIEW, channelUri)
        intent.setPackage("com.android.tv")
        startActivity(intent)
    }


    private fun addDvbChannel(
        channelName: String,
        frequency: Int,
        serviceId: Int,
        inputId: String,
        channelId: Long,
        context: Context
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
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_PREVIEW)
            put(TvContract.Channels.COLUMN_APP_LINK_INTENT_URI, createAppLinkIntentUri(context))
        }

        val newChannelUri = contentResolver.insert(TvContract.Channels.CONTENT_URI, values)
        Log.d("PhilipsTest", "Channel added: $newChannelUri")


        //val channelUri = TvContract.buildChannelUri(channelId)
//        // Use channelUri to update the newly added channel
//        val updateValues = ContentValues().apply {
//            put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, "100")
//        }
//        val rowsUpdated = contentResolver.update(channelUri, updateValues, null, null)
//        Log.d("PhilipsTest", "Rows updated: $rowsUpdated")
    }

    private fun listChannels(context: Context) {
        val contentResolver = context.contentResolver
        //val channelsUri: Uri = TvContract.Channels.CONTENT_URI
        val channelsUri: Uri =
            TvContract.buildChannelsUriForInput(
                "com.mediatek.tvinput/.dtv.TunerInputService/HW0"
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

    private fun tuneChannel(channelUri: Uri) {
        myTvInputService?.getSession()?.onTune(channelUri)
    }

    private fun addSurfaceView() {
        val surfaceView: SurfaceView = findViewById(R.id.surfaceView)

        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                startService(holder.surface)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
    }

    private fun startService(surface: Surface) {
        Log.d("PhilipsTest", "Permission granted. Start service")
        //val intent = Intent(this, MyTvInputService::class.java)
        //intent.putExtra("surface", surface)
        // startService(intent)
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

    private fun addDvbChannel2(channelName: String) {
        val builder = Channel.Builder()

        builder
            .setType(TvContractCompat.Channels.TYPE_PREVIEW)
            .setDisplayName(channelName)

        try {
            val contentValues = builder.build().toContentValues()
            Log.d("PhilipsTest", "ContentValues: $contentValues")

            val channelUri = contentResolver.insert(
                TvContract.PreviewPrograms.CONTENT_URI, contentValues
            )

            Log.d("PhilipsTest", "channelUri: $channelUri")

            if (channelUri == null) {
                Log.e("PhilipsTest", "Failed to insert channel")
                return
            }

            val newChannelId = ContentUris.parseId(channelUri)
            Log.d("PhilipsTest", "New channel ID: $newChannelId")
            addProgram(newChannelId, "appProgramId")
        } catch (e: Exception) {
            Log.e("PhilipsTest", "Error inserting channel", e)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun addProgram(channelId: Long, appProgramId: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com"))
        val intentUri = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

        Log.d("PhilipsTest", "Adding program $channelId $appProgramId $intentUri")

        val builder = PreviewProgram.Builder()
        builder.setChannelId(channelId)
            .setType(TvContractCompat.PreviewPrograms.TYPE_CLIP)
            .setTitle("Title")
            .setDescription("Program description")
            .setPosterArtUri(intentUri)
            .setIntentUri(intentUri)
            .setInternalProviderId(appProgramId)

        try {
            val contentValues = builder.build().toContentValues()
            Log.d("PhilipsTest", "ContentValues: $contentValues")

            val programUri = contentResolver.insert(
                TvContractCompat.PreviewPrograms.CONTENT_URI,
                contentValues
            )

            if (programUri == null) {
                Log.e("PhilipsTest", "Failed to insert program")
            } else {
                Log.d("PhilipsTest", "programUri: $programUri")
            }
        } catch (e: Exception) {
            Log.e("PhilipsTest", "Error inserting program", e)
        }
    }
}


