package com.example.fridgescanner.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastHelper {
    private var toast: Toast? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private var lastToastTime = 0L
    private const val DEBOUNCE_INTERVAL_MS = 2000L // 2 seconds
    /**
     * Shows a Toast message. If a Toast is already being displayed, it updates the text.
     *
     * @param context The context to use. Usually your Application or Activity object.
     * @param message The message to display in the Toast.
     */
    fun showToast(context: Context, message: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastToastTime > DEBOUNCE_INTERVAL_MS) {
            mainHandler.post {
                toast?.cancel()
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).apply {
                    show()
                }
            }
            lastToastTime = currentTime
        }
    }

    /**
     * Optionally, you can provide a method to clear the Toast if needed.
     */
    fun clearToast() {
        mainHandler.post {
            toast?.cancel()
            toast = null
        }
    }
}
