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
import android.util.Log


class MyTvInputService : TvInputService() {
    @SuppressLint("Range", "Recycle")
    override fun onCreate() {
        super.onCreate()
        Log.d("PhilipsTest", "MyTvInputService created")

        val tvInputId = getTvInputId()

        Log.d(
            "PhilipsTest", "TV Input Manager: $tvInputId"
        )

        if (tvInputId == null) {
            Log.d("PhilipsTest", "HW input not found")
            return
        }

        val session = this.onCreateSession(tvInputId) as MySession

        val resolver = contentResolver
        val cursor = resolver.query(TvContract.Channels.CONTENT_URI, null, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val channelId = cursor.getLong(cursor.getColumnIndex(TvContract.Channels._ID))
                val channelUri = TvContract.buildChannelUri(channelId)
                Log.d("PhilipsTest", "Channel URI: $channelUri")
                session.notifyChannelRetuned(channelUri)
            } while (cursor.moveToNext())
        }
    }

    override fun onCreateSession(inputId: String): Session {
        Log.d("PhilipsTest", "Creating session for input: $inputId")
        Log.d("PhilipsTest", "Context $this")
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
        val tvInput = tvInputManager?.tvInputList?.find { it.id.contains("HW0") }

        return tvInput?.id
    }

    fun onReleaseSession(session: Session) {
        Log.d("PhilipsTest", "Releasing session")
    }

    companion object {
        const val CHANNEL_ID = "MyTvInputServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}