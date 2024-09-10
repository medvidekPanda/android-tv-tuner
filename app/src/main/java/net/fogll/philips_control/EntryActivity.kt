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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EntryActivity : AppCompatActivity() {
    private var myTvInputService: MyTvInputService? = null
    private val intentId: Array<String> = arrayOf(
        "org.droidtv.playtv",
    )

    private val frequency: Array<Long> = arrayOf(
        274,
        554
    )

    private val serviceId: Array<Int> = arrayOf(
        101,
        102
    )

    private val tsId: Array<Int> = arrayOf(
        1,
        2,
        1001 // DVB-T
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        val inputId = "com.mediatek.tvinput/.dtv.TunerInputService/HW0"
        //listChannels3(inputId)

        forceSyncChannels(inputId)
        //listChannels3("org.droidtv.playtv/.ChannelInput")

        // list1(inputId)


//        addDvbChannel(
//            "CT 1 test 5",
//            frequency[1],
//            serviceId[0],
//            tsId[2],
//            inputId
//        )

        //startService()
//        findInputId()
//        contentResolver.notifyChange(TvContract.Channels.CONTENT_URI, null)
        //listChannels(inputId)
        //getChannelInfo(4277)
        //getChannelInfo(4286)

//        val channelId = ContentUris.parseId("content://android.media.tv/channel/4282".toUri())
//        Log.d("PhilipsTest", "Channel ID: $channelId")
//        val channelUri = TvContract.buildChannelUri(channelId)
//        val intent = Intent(Intent.ACTION_VIEW, channelUri)
//        Log.d("PhilipsTest", "intent: $intent")
//        intent.setPackage(intentId[0])
//        startActivity(intent)
    }

    private fun getChannelInfo(channelId: Long) {
        val channelUri = TvContract.buildChannelUri(channelId)

        val cursor = contentResolver.query(
            channelUri,
            null, // Projection (null for all columns)
            null, // Selection
            null, // Selection arguments
            null  // Sort order
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val channelName =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_DISPLAY_NAME))
                val channelNumber =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_DISPLAY_NUMBER))
                val inputId =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_INPUT_ID))
                // ... get other channel details

                Log.d("PhilipsTest", "Name: $channelName, Number: $channelNumber, Input: $inputId")
            } else {
                Log.d("PhilipsTest", "Channel not found")
            }
        }
    }

    private fun addDvbChannel(
        channelName: String,
        frequency: Long,
        serviceId: Int,
        transportStreamId: Int,
        inputId: String,
    ) {


        val values = ContentValues().apply {
            put(TvContract.Channels.COLUMN_INPUT_ID, inputId)
            put(TvContract.Channels.COLUMN_DISPLAY_NAME, channelName)
            put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, frequency * 1000000)
            put(TvContract.Channels.COLUMN_SERVICE_ID, serviceId)
            put(TvContract.Channels.COLUMN_TYPE, TvContract.Channels.TYPE_DVB_T)
            put(TvContract.Channels.COLUMN_NETWORK_AFFILIATION, "SATT")
            put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, transportStreamId)
            put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, serviceId)
            put(TvContract.Channels.COLUMN_VIDEO_FORMAT, TvContract.Channels.VIDEO_FORMAT_1080I)
            put(TvContract.Channels.COLUMN_DESCRIPTION, "Channel Description")
            //put("modulation", "QAM64") // Custom column for modulation
            //put("symbol_rate", 6900000) // Custom column for symbol rate
        }

        val newChannelUri = contentResolver.insert(TvContract.Channels.CONTENT_URI, values)
        //contentResolver.notifyChange(TvContract.Channels.CONTENT_URI, null)
        contentResolver.notifyChange(newChannelUri!!, null)
        Log.d("PhilipsTest", "Channel added: $newChannelUri")
    }

    private fun updateChannel() {

    }

    private fun list1(tvInputId: String) {
        val contentResolver = this.contentResolver
        val channelsUri: Uri = TvContract.Channels.CONTENT_URI

        val cursor = contentResolver.query(channelsUri, null, null, null, null)
        val channelList = StringBuilder()

        cursor?.use {
            while (it.moveToNext()) {
                val channelName =
                    it.getString(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_DISPLAY_NAME))
                Log.d("PhilipsTest", "Channel: $channelName")
                // Access other channel properties using getColumnIndexOrThrow()
            }
        }
    }

    private fun listChannels(inputId: String) {
        val contentResolver = this.contentResolver
        //val channelsUri: Uri = TvContract.Channels.CONTENT_URI
        val channelsUri: Uri = TvContract.buildChannelsUriForInput(
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
                val type = it.getInt(it.getColumnIndexOrThrow(TvContract.Channels.COLUMN_TYPE))

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

    private fun findInputId(): String? {
        val tvInputManager = this.getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        val tvInputs = tvInputManager?.tvInputList ?: return null

        for (input in tvInputs.filter { it.id.contains("com.mediatek.tvinput") }) {
            Log.d("PhilipsTest", "Input ID: ${input.id}")
            Log.d("PhilipsTest", "Type: ${input.type}")
            Log.d("PhilipsTest", "----------Input Name-------------: ${input.loadLabel(this)}")

            if (input.type == TvInputInfo.TYPE_TUNER) {
                //   return input.id
            }
        }
        return null
    }

    private fun listChannels3(inputId: String) {
        val tvInputManager = getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
        val inputs = tvInputManager.tvInputList

        for (input in inputs) {
            Log.d("PhilipsTest", "Hledání programů pro vstup: ${input.id}")
            val uri = TvContract.buildChannelsUriForInput(input.id)
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use { channelCursor ->
                while (channelCursor.moveToNext()) {
                    val channelId =
                        channelCursor.getLong(channelCursor.getColumnIndexOrThrow(TvContract.Channels._ID))
                    val channelUri = TvContract.buildProgramsUriForChannel(channelId)
                    val programCursor = contentResolver.query(channelUri, null, null, null, null)
                    programCursor?.use {
                        Log.d("PhilipsTest", "Nalezeno ${it.count} programů pro kanál $channelId")
                        while (it.moveToNext()) {
                            val title =
                                it.getString(it.getColumnIndexOrThrow(TvContract.Programs.COLUMN_TITLE))
                            val startTime =
                                it.getLong(it.getColumnIndexOrThrow(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS))
                            val endTime =
                                it.getLong(it.getColumnIndexOrThrow(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS))
                            Log.d(
                                "PhilipsTest",
                                "Program: $title, Start: $startTime, End: $endTime"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun forceSyncChannels(inputId: String) {
        val uri = Uri.parse("content://org.droidtv.playtv.provider/channel")
        val projection = arrayOf("_id", "name", "number", "input_id")

        try {
            val cursor = contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                Log.d("PhilipsTest", "Nalezeno ${it.count} Philips kanálů")
                while (it.moveToNext()) {
                    val id = it.getLong(it.getColumnIndexOrThrow("_id"))
                    val name = it.getString(it.getColumnIndexOrThrow("name"))
                    val number = it.getString(it.getColumnIndexOrThrow("number"))
                    val inputId = it.getString(it.getColumnIndexOrThrow("input_id"))
                    Log.d(
                        "PhilipsTest",
                        "Philips Kanál: ID: $id, Název: $name, Číslo: $number, Input: $inputId"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PhilipsTest", "Chyba při získávání Philips kanálů: ${e.message}")
        }
    }
}


