package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat


fun NotificationManager.sendNotification(context: Context, title: String, message: String, file: DownloadFile, status: DownloadStatus) {
    val actionIntent = Intent(context, DetailActivity::class.java)
    actionIntent.putExtra(DOWNLOADED_FILE_INTENT_EXTRA_NAME, file.selection)
    actionIntent.putExtra(DOWNLOADED_STATUS_INTENT_EXTRA_NAME, status.status)

    val actionPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        actionIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val channelId = context.getString(R.string.notification_channel_id)
    val actionTitle = context.getString(R.string.notification_button)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_download)
        .setContentTitle(title)
        .setContentText(message)
        .addAction(NotificationCompat.Action(null, actionTitle, actionPendingIntent))
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun createFileDownloadNotificationChannel(context: Context) {
    val channelId =  context.getString(R.string.notification_channel_id)
    val channelName = context.getString(R.string.notification_channel_name)
    val channelDescription = context.getString(R.string.notification_channel_description)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            .apply {
                setShowBadge(false)
            }

        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = channelDescription

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        notificationManager.createNotificationChannel(notificationChannel)
    }
}