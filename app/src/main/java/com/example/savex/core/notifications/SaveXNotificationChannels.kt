package com.example.savex.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object SaveXNotificationChannels {
    const val REVIEW_REMINDERS = "review_reminders"
    const val GENERAL_UPDATES = "general_updates"

    fun create(context: Context) {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            return
//        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel(
                REVIEW_REMINDERS,
                "Review reminders",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Notifications for spaced-repetition reminders."
            },
            NotificationChannel(
                GENERAL_UPDATES,
                "General updates",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Background sync and app status updates."
            },
        )

        notificationManager.createNotificationChannels(channels)
    }
}
