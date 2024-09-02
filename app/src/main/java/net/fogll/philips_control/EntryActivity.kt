package net.fogll.philips_control

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.tv.TvInputManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class EntryActivity : AppCompatActivity() {
    private val permissionRequestReadTvListings = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PhilipsTest", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        // Check if the READ_TV_LISTINGS permission is already granted
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_TV_LISTINGS")
            != PackageManager.PERMISSION_GRANTED
        ) {
            // If not, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.READ_TV_LISTINGS"),
                permissionRequestReadTvListings
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("PhilipsTest", "------------onStart-------------")

        val intent = Intent(this, MyTvInputService::class.java)
        startForegroundService(intent)
    }
}


