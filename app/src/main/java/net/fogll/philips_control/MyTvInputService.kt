package net.fogll.philips_control

import android.content.Intent
import android.media.tv.TvInputService
import android.os.IBinder
import android.util.Log


class MyTvInputService : TvInputService() {
    override fun onCreate() {
        super.onCreate()
        Log.d("TVInput", "MyTvInputService created")
    }

    override fun onCreateSession(inputId: String): Session {
        Log.d("TVInput", "Creating session for input: $inputId")
        return MySession(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TVInput", "MyTvInputService started")
        // Your service logic here
        return START_STICKY
    }

    fun onReleaseSession(session: Session) {
        Log.d("TVInput", "Releasing session")
    }
}