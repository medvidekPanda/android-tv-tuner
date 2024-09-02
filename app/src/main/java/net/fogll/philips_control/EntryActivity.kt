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
        Log.d("TVInput", "------------onCreate-------------")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        val tvInputManager = this.getSystemService(Context.TV_INPUT_SERVICE) as TvInputManager
        val tvInput = tvInputManager.tvInputList.find { it.id.contains("HW0") }

        Log.d(
            "TVInput", "TV Input Manager: ${tvInput?.id} - ${tvInput?.serviceInfo} - ${
                tvInput?.loadLabel(
                    this
                )
            }"
        )

        if (tvInput?.id == null) {
            Log.d("TVInput", "HW input not found")
            return;
        }

        val tvInputService = MyTvInputService()
        val session = tvInputService.onCreateSession("id")
        val intent = Intent(this, MyTvInputService::class.java)
        startService(intent)

        //val channelUri = Uri.parse("content://net.fogll.philips_control/channel/1")

        //session.onTune(channelUri)


    }
}


