// RegisterScreen.kt
package com.example.fridgescanner.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: FridgeViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Control visibility for password fields.
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Called when the Register button is clicked.
    fun onRegisterClicked() {

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
            return
        }

        viewModel.name = username

        // Call the ViewModel's registerUser function.
        viewModel.registerUser(username, email, password) { success, message ->
            // Switch back to the main thread to update the UI.
            if (success) {
                // Registration was successful.
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_LONG).show()
                // Navigate directly to the HomePageScreen.
                navController.navigate(Screen.HomePageScreen.withArgs(username)) {
                    // Optionally clear the back stack to prevent going back to the login/registration screens.
                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
                }
            } else {
                // Registration failed; show an error message.
                Toast.makeText(context, message ?: "Registration failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Username
        Text(text = "Username")
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Your desired username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
        )

        // Email
        Text(text = "Email")
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("your.email@domain.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
        )

        // Password
        Text(text = "Password")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Choose a password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible)
                    Icons.Filled.VisibilityOff
                else
                    Icons.Filled.Visibility

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle password visibility")
                }
            }
        )

        // Confirm Password Field
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Confirm Password")
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("Confirm your password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (confirmPasswordVisible)
                    Icons.Filled.VisibilityOff
                else
                    Icons.Filled.Visibility
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Toggle confirm password visibility")
                }
            }
        )

        // Register Button
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onRegisterClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Register")
        }

        // Back to Login link
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back to Login")
        }
    }
}
