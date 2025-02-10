package com.example.fridgescanner.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
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
        // Display account details and the logout button.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "${viewModel.name}'s Account",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            // This spacer pushes the Logout button to the bottom of the screen.
            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = {
                    // Call your logout logic.
                    // For example, you might clear any saved session in your ViewModel.
                    viewModel.logout()  // Ensure you implement this in your ViewModel.

                    // Then navigate to the login screen.
                    navController.navigate(Screen.LoginScreen.route) {
                        // Clear back stack so that the user cannot go back to a logged-in screen.
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
