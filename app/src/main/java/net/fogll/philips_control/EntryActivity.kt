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

        val tvInputManager = getSystemService(Context.TV_INPUT_SERVICE) as? TvInputManager
        if (tvInputManager == null) {
            Log.e("PhilipsTest", "TvInputManager is null")
            return
        }
        val tvInput = tvInputManager.tvInputList.find { it.id.contains("HW0") }

        Log.d(
            "PhilipsTest", "TV Input Manager: ${tvInput?.id} - ${tvInput?.serviceInfo} - ${
                tvInput?.loadLabel(
                    this
                )
            }"
        )

//        if (tvInput?.id == null) {
//            Log.d("PhilipsTest", "HW input not found")
//            return;
//        }

        val tvInputService = MyTvInputService()
        val session = tvInputService.onCreateSession("tvInput.id") as MySession
        val intent = Intent(this, MyTvInputService::class.java)
        startForegroundService(intent)

        session.getTunedFrequency(this)
    }
}


