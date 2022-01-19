package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        radioGroup = findViewById(R.id.rdgrp_downloads)

        // create channel
        createFileDownloadNotificationChannel(this)
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val id: Long = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) as Long
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))

                val file: DownloadFile = when (uri) {
                    getString(R.string.url_glide) -> DownloadFile.Glide
                    getString(R.string.url_repo) -> DownloadFile.Repo
                    getString(R.string.url_retrofit) -> DownloadFile.Retrofit
                    else -> DownloadFile.Undefined
                }

                val result: DownloadStatus = when (status) {
                   DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.Succeed
                   DownloadManager.STATUS_FAILED -> DownloadStatus.Failed
                   else -> DownloadStatus.Undefined
                }

                cursor.close()

                sentDownloadNotification(file, result)
            }

            setDownloadCompleted()
        }

    }

    private fun setDownloadCompleted() {
        custom_button.setCompleted()
    }

    private fun sentDownloadNotification(file: DownloadFile, result: DownloadStatus) {
        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager

        notificationManager.sendNotification(this, getString(R.string.notification_title), getString(R.string.notification_description), file, result)
    }

    private fun download() {
        val url = when(radioGroup.checkedRadioButtonId) {
            R.id.rdbtn_glide -> getString(R.string.url_glide)
            R.id.rdbtn_load_app -> getString(R.string.url_repo)
            R.id.rdbtn_retrofit -> getString(R.string.url_retrofit)
            else -> ""
        }

        if (url.isEmpty()) {
            Toast.makeText(this, R.string.no_file_msg, Toast.LENGTH_SHORT).show()

            setDownloadCompleted()
        } else {
            val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request) // enqueue puts the download request in the queue.
        }
    }

}
