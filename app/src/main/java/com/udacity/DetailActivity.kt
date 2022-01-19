package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)

        setupOkButtonClick()
        setupTextData()
    }

    private fun setupOkButtonClick() {
        (findViewById<Button>(R.id.btn_ok) as Button).setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
    }

    private fun setupTextData() {
        val file = intent.getIntExtra(DOWNLOADED_FILE_INTENT_EXTRA_NAME, -1)
        val status = intent.getIntExtra(DOWNLOADED_STATUS_INTENT_EXTRA_NAME, -1)

        val fileNameTxt = when(file) {
            DownloadFile.Glide.selection -> getString(R.string.radio_glide_option)
            DownloadFile.Repo.selection -> getString(R.string.radio_load_app_option)
            DownloadFile.Retrofit.selection -> getString(R.string.radio_retrofit_option)
            else -> "Unknown"
        }

        val statusTxt = when(status) {
            DownloadStatus.Succeed.status -> "Succeeded"
            DownloadStatus.Failed.status -> "Failed"
            else -> "Error"
        }

        (findViewById<TextView>(R.id.txt_file_name)).text = fileNameTxt
        (findViewById<TextView>(R.id.txt_file_status)).text = statusTxt
    }

}
