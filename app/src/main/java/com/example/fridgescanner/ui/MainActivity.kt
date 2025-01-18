package com.example.fridgescanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fridgescanner.data.FridgeRepository
import com.example.fridgescanner.ui.notification.createNotificationChannel
import com.example.fridgescanner.ui.notification.scheduleFridgeNotifications
import com.example.fridgescanner.ui.theme.FridgeScannerTheme
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.example.fridgescanner.viewmodel.FridgeViewModelFactory


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
            // 1) Create / Provide the repository & ViewModel
            val repository = FridgeRepository()
            val viewModelFactory = FridgeViewModelFactory(repository)
            val fridgeViewModel: FridgeViewModel = viewModel(factory = viewModelFactory)

            // 2) Observe dark mode from the ViewModel
            val isDarkMode = fridgeViewModel.darkModeEnabled.collectAsState(initial = false).value

            // 3) Wrap Navigation in your theme, toggling dark mode
            FridgeScannerTheme(darkTheme = isDarkMode) {
                Navigation(fridgeViewModel)
            }
        }
    }
}

