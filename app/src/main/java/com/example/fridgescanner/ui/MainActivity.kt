package com.example.fridgescanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fridgescanner.ui.notification.createNotificationChannel
import com.example.fridgescanner.ui.notification.scheduleFridgeNotifications


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Thread.sleep(3000)
        installSplashScreen()

        // 1) Create a notification channel
        createNotificationChannel(this)

        // 2) Schedule background checks daily
        scheduleFridgeNotifications(this)

        setContent {
            Navigation()
        }
    }
}

