// ScheduleWorker.kt
package com.example.fridgescanner.ui.notification

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

fun scheduleFridgeNotifications(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<FridgeNotificationWorker>(1, TimeUnit.DAYS)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "FridgeNotifications",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}
