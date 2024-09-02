package net.fogll.philips_control

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.tv.TvContract
import android.media.tv.TvInputInfo
import android.media.tv.TvInputManager
import android.media.tv.TvInputService
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class EntryActivity : AppCompatActivity() {

    //private val permissionReadListings = "com.android.providers.tv.permission.READ_TV_LISTINGS"
    private val permissionReadListings = "android.permission.ACCESS_COARSE_LOCATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TVInput", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)


//        if (ContextCompat.checkSelfPermission(
//                this,
//                permissionReadListings,
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("TVInput", "Permission not granted")
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(permissionReadListings),
//                1
//            )
//        } else {
//            Log.d("TVInput", "Permission granted")
//        }


        val intent = Intent(this, MyTvInputService::class.java)
        Log.d("TVInput", "Starting TV input service")

        val channelUri = Uri.parse("content://net.fogll.tvtunertest/channel/1")
        val tvInputService = MyTvInputService()
        val session = tvInputService.onCreateSession("inputId")
        //session.onTune(channelUri)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("TVInput", "Permission granted")

            val tvInputManager = this.getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
            val tvInput =
                tvInputManager.tvInputList.find { it.id.contains("HW0") }

            Log.d(
                "TVInput",
                "TV Input Manager: ${tvInput?.id} - ${tvInput?.serviceInfo} - ${
                    tvInput?.loadLabel(
                        this
                    )
                }"
            )

            Log.d("TVInput", "State: ${tvInputManager.getInputState(tvInput?.id!!)}")
            queryChannels()
        } else {
            Log.e("TVInput", "Permission denied")
        }
    }

    @SuppressLint("Range")
    private fun queryChannels() {
        val uri = TvContract.Channels.CONTENT_URI
        val cursor = this.contentResolver.query(uri, null, null, null, null)

        Log.d("TVInput", "Uri: $uri")
        Log.d("TVInput", "Cursor: ${cursor?.moveToFirst()}")

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val channelId = cursor.getLong(cursor.getColumnIndex(TvContract.Channels._ID))
                val channelName =
                    cursor.getString(cursor.getColumnIndex(TvContract.Channels.COLUMN_DISPLAY_NAME))
                Log.d("ChannelList", "Channel: $channelName (ID: $channelId)")
            }
            cursor.close()
        }
    }
}