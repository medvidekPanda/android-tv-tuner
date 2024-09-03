package net.fogll.philips_control

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.media.tv.TvContract
import android.net.Uri

class EntryActivity : AppCompatActivity() {
    private val REQUEST_CODE_READ_TV_LISTINGS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        startService()
    }

    private fun startService() {
        Log.d("PhilipsTest", "Permission granted. Start service")
        val intent = Intent(this, MyTvInputService::class.java)
        startService(intent)
    }
}


