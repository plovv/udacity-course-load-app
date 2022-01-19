package com.udacity

enum class DownloadFile(val selection: Int) {
    Undefined(-1), Glide(1), Repo(2), Retrofit(3)
}

enum class DownloadStatus(val status: Int) {
    Undefined(-1), Succeed(1), Failed(2)
}

const val NOTIFICATION_ID = 1

const val DOWNLOADED_FILE_INTENT_EXTRA_NAME = "DownloadedFile"
const val DOWNLOADED_STATUS_INTENT_EXTRA_NAME = "DownloadStatus"