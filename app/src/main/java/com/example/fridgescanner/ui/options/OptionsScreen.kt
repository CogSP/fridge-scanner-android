// OptionsScreen.kt
package com.example.fridgescanner.ui.options

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.viewmodel.FridgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    navController: NavController,
    viewModel: FridgeViewModel
) {
    val notificationsEnabled by remember { mutableStateOf(true) }
    val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
    val currentThreshold by viewModel.expirationThreshold.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Options") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Notifications
            Text(text = "Fridge Sharing", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Fridge Sharing", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { /* handle notifications logic */ }
                )
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // Appearance
            Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }

            Divider(color = Color.Gray.copy(alpha = 0.3f))

            // Expiration Threshold
            Text(text = "Expiration Threshold", style = MaterialTheme.typography.titleMedium)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Expiring Soon in $currentThreshold day(s).",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = currentThreshold.toFloat(),
                    onValueChange = { newVal -> viewModel.setExpirationThreshold(newVal.toLong()) },
                    valueRange = 0f..14f,
                    steps = 14
                )
            }
        }
    }
}
