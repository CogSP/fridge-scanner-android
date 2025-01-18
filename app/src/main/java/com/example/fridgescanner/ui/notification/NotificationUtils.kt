// NotificationUtils.kt
package com.example.fridgescanner.ui.notification

import android.content.Context
import com.example.fridgescanner.data.FridgeItem
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.fridgescanner.ui.MainActivity
import com.example.fridgescanner.R // If you have a custom icon

private const val CHANNEL_ID = "fridge_notifications"
private const val CHANNEL_NAME = "Fridge Notifications"

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications about expired/expiring fridge items."
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}



fun sendLocalNotification(
    context: Context,
    title: String,
    message: String
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // or your own icon
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true) // Dismiss notification on tap
        .build()

    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), notification)
}


fun checkFridgeItemsAndNotify(context: Context, items: List<FridgeItem>) {
    val thresholdDays = 3L // e.g., notify if items will expire within 3 days

    items.forEach { item ->
        when {
            item.isExpired() -> {
                sendLocalNotification(
                    context,
                    "Item Expired!",
                    "${item.name} has expired."
                )
            }
            item.isExpiringSoon(thresholdDays) -> {
                sendLocalNotification(
                    context,
                    "Item Expiring Soon",
                    "${item.name} will expire within $thresholdDays day(s)."
                )
            }
        }
    }
}
