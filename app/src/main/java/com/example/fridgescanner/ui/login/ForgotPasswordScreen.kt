package com.example.fridgescanner.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: FridgeViewModel) {
    // State for email or username input.
    var email by remember { mutableStateOf("") }
    // State for feedback message.
    var message by remember { mutableStateOf("") }
    // Loading state while calling the API.
    var isLoading by remember { mutableStateOf(false) }

    // Called when the user taps the "Reset Password" button.
    fun onResetPasswordClicked() {
        if (email.isBlank()) {
            message = "Please enter your email"
            return
        }
        isLoading = true
        // Call the forgotPassword function in your ViewModel.
        viewModel.forgotPassword(email) { success, responseMessage ->
            isLoading = false
            message = responseMessage ?: ""
            if (success) {
                // On success, navigate to a ResetPasswordScreen (pass the email)
                navController.navigate(Screen.ResetPasswordScreen.withArgs(email))
            }
        }
    }

    // Layout for the Forgot Password screen.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Reset Your Password",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Instruction text.
        Text(text = "Enter your Email or Username")

        // Input field.
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("email@domain.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
        )

        // Loading indicator (optional).
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Reset Password Button.
        Button(
            onClick = { onResetPasswordClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Reset Password")
        }

        // Show any feedback message.
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = if (message.contains("success", true)) Color.Green else Color.Red
            )
        }

        // Link to go back to login.
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back to Login")
        }
    }
}
