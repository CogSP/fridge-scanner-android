package com.example.fridgescanner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel


@Composable
fun DetailScreen(name: String?, navController: NavController, viewModel: FridgeViewModel) {
    // A scaffold provides a flexible structure with a top bar, content area, and bottom bar if needed.
    Scaffold(
        topBar = {
            DetailScreenTopBar(name)
        },
        content = { innerPadding ->
            // Main content area
            DetailScreenContent(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreenTopBar(name: String?) {
    TopAppBar(
        title = {
            // If the name is null or blank, we can handle that gracefully
            val userName = if (name.isNullOrBlank()) "Guest" else name
            Text(text = "Hello, $userName!")
        }
    )
}

@Composable
fun DetailScreenContent(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        WelcomeCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Three custom-styled buttons at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Scan New Item Button
            FilledTonalButton(
                onClick = {
                    navController.navigate(Screen.ScanScreen.route)
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan Icon",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("Scan")
            }

            // 2. Fridge Button
            Button(
                onClick = { navController.navigate(Screen.FridgeScreen.route) },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(56.dp)  // Same height
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Kitchen,
                    contentDescription = "Fridge Icon",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("Fridge")
            }

            // 3. Options Button
            ElevatedButton(
                onClick = { navController.navigate(Screen.OptionsScreen.route) },
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(56.dp)  // Same height
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Options Icon",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("Options")
            }
        }
    }
}


@Composable
fun WelcomeCard() {
    // A simple card greeting the user. You could customize it with an icon,
    // an image, or other UI elements to make it look more appealing.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Welcome to the App!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check your fridge items, scan something new, or explore options.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
