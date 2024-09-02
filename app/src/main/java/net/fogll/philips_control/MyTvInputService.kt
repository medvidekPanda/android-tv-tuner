package net.fogll.philips_control

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.tv.TvInputService
import android.os.Build
import android.os.IBinder
import android.util.Log


class MyTvInputService : TvInputService() {
    override fun onCreate() {
        super.onCreate()
        Log.d("PhilipsTest", "MyTvInputService created")
    }

    override fun onCreateSession(inputId: String): Session {
        Log.d("PhilipsTest", "Creating session for input: $inputId")
        return MySession(this)
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("MyTvInputService")
            .setContentText("Service is running in the foreground")
            .build()

        startForeground(NOTIFICATION_ID, notification)

        // Your service logic here

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    fun onReleaseSession(session: Session) {
        Log.d("PhilipsTest", "Releasing session")
    }

    companion object {
        const val CHANNEL_ID = "MyTvInputServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}