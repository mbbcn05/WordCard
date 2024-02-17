package com.babacan05.wordcard.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import com.babacan05.wordcard.R
import com.babacan05.wordcard.model.WordCard

object NotificationUtils {
    private const val CHANNEL_ID = "My_Channel_Id"
    private const val CHANNEL_NAME = "My Channel"

    fun showNotification(context: Context, card: WordCard) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "My channel description"
                enableLights(true)
                lightColor = Color.Blue.toArgb()

            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)

            .setContentTitle("Your next word...")
            .setContentText(card.word.uppercase())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}


