package net.fogll.philips_control

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Surface
import androidx.core.net.toUri

class MyTvInputService : TvInputService() {
    private var session: MySession? = null
    private var playbackSurface: Surface? = null

    @SuppressLint("Range", "Recycle")
    override fun onCreate() {
        super.onCreate()
        Log.d("PhilipsTest", "MyTvInputService created")

        val tvInputId = getTvInputId()
        if (tvInputId == null) {
            Log.d("PhilipsTest", "HW input not found")
        }

        onCreateSession("tvInputId")
        Log.d("PhilipsTest", "Session created $session")
        session?.onTune("content://android.media.tv/channel/2956".toUri())

        val contentResolver = contentResolver
        val channelUri = Uri.parse("content://android.media.tv/channel/2956")

        val cursor = contentResolver.query(
            channelUri,
            null, // Projection (retrieve all columns)
            null, // Selection
            null, // Selection arguments
            null  // Sort order
        )

        if (cursor != null && cursor.moveToFirst()) {
            Log.d("PhilipsTest", "Channel found for URI: $channelUri")
        } else {
            Log.e("PhilipsTest", "No channel found for URI: $channelUri")
        }

        cursor?.close()
    }

    override fun onCreateSession(inputId: String): Session {
        Log.d("PhilipsTest", "Creating session for input: $inputId")
        session = MySession(this)
        playbackSurface?.let { session?.onSetSurface(it) }
        return session!!
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification =
            Notification.Builder(this, CHANNEL_ID).setContentTitle("MyTvInputService")
                .setContentText("Service is running in the foreground").build()

        startForeground(NOTIFICATION_ID, notification)

        playbackSurface = intent?.getParcelableExtra("surface")
        playbackSurface?.let { session?.onSetSurface(it) }

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
        if (this.session === session) {
            this.session?.onRelease() // Ensure session is released
            this.session = null
        }
    }

    fun getSession(): MySession? {
        Log.d("PhilipsTest", "Getting session")
        return session
    }

    companion object {
        var instance: MyTvInputService? = null
        const val CHANNEL_ID = "MyTvInputServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    fun setTimeout(delay: Long, task: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(task, delay)
    }
}