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
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.Surface

class MyTvInputService : TvInputService() {
    private var session: MySession? = null
    private var playbackSurface: Surface? = null

    inner class LocalBinder : Binder() {
        fun getService(): MyTvInputService = this@MyTvInputService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("PhilipsTest", "MyTvInputService created")
        val tvInputId = getTvInputId()
//        if (tvInputId == null) {
//            Log.d("PhilipsTest", "HW input not found")
//        }
        onCreateSession(tvInputId ?: "defaultInputId")
        Log.d("PhilipsTest", "Session created $session")
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
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("MyTvInputService")
            .setContentText("Service is running in the foreground")
            .build()

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
            this.session?.onRelease()
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
}