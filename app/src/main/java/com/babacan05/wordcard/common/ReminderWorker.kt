package com.babacan05.wordcard.common

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        showReminderNotification(applicationContext)
        return Result.success()
    }

    private fun showReminderNotification(context: Context) {
       NotificationUtils.showNotification(context = context,"your word","your title")
    }
}