package net.fogll.philips_control

import android.content.Context
import android.content.Intent
import android.media.tv.TvInputManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class EntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
    }

    override fun onStart() {
        super.onStart()
        Log.d("PhilipsTest", "------------onStart-------------")

        val intent = Intent(this, MyTvInputService::class.java)
        startForegroundService(intent)
    }
}


