package com.example.fridgescanner.ui

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.viewmodel.FridgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController, viewModel: FridgeViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Display account details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Account Details",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Display user's name from ViewModel
            Text(
                text = "Name: ${viewModel.name}",
                style = MaterialTheme.typography.bodyLarge
            )
            // Add additional account fields as needed, for example:
            // Text(text = "Email: ${viewModel.email}", style = MaterialTheme.typography.bodyLarge)
            // ...
        }
    }
}
