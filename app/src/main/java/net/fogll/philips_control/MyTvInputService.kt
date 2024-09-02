package net.fogll.philips_control

import android.media.tv.TvInputService
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

    fun onReleaseSession(session: Session) {
        Log.d("TVInput", "Releasing session")
    }
}