package net.fogll.philips_control

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.net.Uri
import android.util.Log


class MyTvInputService : TvInputService() {
    @SuppressLint("Range", "Recycle")
    override fun onCreate() {
        super.onCreate()
        Log.d("PhilipsTest", "MyTvInputService created")

        //val packageName = this.packageName
        //val frequencyUri = Uri.parse("content://$packageName/channel?frequency=274000000")
        val frequencyUri =
            Uri.parse("content://android.media.tv/channel?frequency=274000000")
        Log.d("PhilipsTest", "Frequency URI: $frequencyUri")

        val tvInputId = getTvInputId()

        Log.d(
            "PhilipsTest", "TV Input Manager: $tvInputId"
        )

        if (tvInputId == null) {
            Log.d("PhilipsTest", "HW input not found")
            return
        }

        val session = this.onCreateSession(tvInputId) as MySession
        val tuned = session.onTune(frequencyUri)

        if (tuned) {
            //fetchChannels(274000000)
        } else {
            Log.d("PhilipsTest", "Failed to tune to frequency")
        }
    }

    override fun onCreateSession(inputId: String): Session {
        Log.d("PhilipsTest", "Creating session for input: $inputId")
        return MySession(this)
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification =
            Notification.Builder(this, CHANNEL_ID).setContentTitle("MyTvInputService")
                .setContentText("Service is running in the foreground").build()

        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun getTvInputId(): String? {
        val tvInputManager = getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        val tvInput = tvInputManager?.tvInputList?.find { it.id.contains("HW9") }
        return tvInput?.id
    }

    fun onReleaseSession(session: Session) {
        Log.d("PhilipsTest", "Releasing session")
    }

    companion object {
        const val CHANNEL_ID = "MyTvInputServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    @SuppressLint("Recycle", "Range")
    private fun fetchChannels(frequency: Int) {
        val channelsUri: Uri = TvContract.Channels.CONTENT_URI
        val projection = arrayOf(
            TvContract.Channels.COLUMN_DISPLAY_NAME,
            TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID,
            TvContract.Channels.COLUMN_SERVICE_ID
        )

        Log.d("PhilipsTest", "channelsUri: $channelsUri")

        val cursor = this.contentResolver.query(channelsUri, null, null, null, null)

        // Log cursor count
        Log.d("PhilipsTest", "Cursor count: ${cursor?.count ?: 0}")

        cursor?.use {
            while (it.moveToFirst()) {
                //val channelName = it.getString(it.getColumnIndex(TvContract.Channels.DISPLAY_NAME))
                Log.d("PhilipsTest", "Channel: $it")
            }
        }
    }
}