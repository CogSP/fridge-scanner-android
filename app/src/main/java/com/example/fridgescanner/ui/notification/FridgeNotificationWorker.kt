// FridgeNotificationWorker.kt
package com.example.fridgescanner.ui.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fridgescanner.data.FridgeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FridgeNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repository: FridgeRepository // You may pass this differently
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // 1) Retrieve items
        val items = withContext(Dispatchers.IO) {
            repository.getFridgeItems()
        }
        // 2) Check and notify
        checkFridgeItemsAndNotify(applicationContext, items)

        return Result.success()
    }
}
