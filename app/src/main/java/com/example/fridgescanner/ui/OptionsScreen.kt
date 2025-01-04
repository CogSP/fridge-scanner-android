package com.example.fridgescanner.ui

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
    viewModel: FridgeViewModel // Access the same ViewModel that holds the threshold
) {
    // Example states for toggles or other user preferences
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    // Collect the current threshold from the ViewModel
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

            // Notification Settings
            Text(text = "Notifications", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Enable Notifications", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            // Dark Mode Option
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dark Mode", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }

            // Expiration Threshold Section
            Divider(color = Color.Gray.copy(alpha = 0.3f))
            Text(text = "Expiration Threshold", style = MaterialTheme.typography.titleMedium)

            // A row with a slider to pick the threshold in days
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Items will be considered 'expiring soon' within $currentThreshold day(s).",
                    style = MaterialTheme.typography.bodyMedium
                )
                Slider(
                    value = currentThreshold.toFloat(),
                    onValueChange = { newVal ->
                        viewModel.setExpirationThreshold(newVal.toLong())
                    },
                    valueRange = 0f..14f,
                    steps = 14 // for integer steps from 0..14
                )
            }
        }
    }
}
