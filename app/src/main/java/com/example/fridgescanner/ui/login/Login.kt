// LoginScreen.kt
package com.example.fridgescanner.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.R
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel
import android.util.Log


@Composable
fun LoginScreen(navController: NavController, viewModel: FridgeViewModel) {
    // State for user input
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Whether the password is visible or masked
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // State for error messages.
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // Called when the Login button is clicked.
    fun onLoginClicked() {
        // Clear previous error message.
        errorMessage = null

        // Call the loginUser function defined in the ViewModel.
        viewModel.loginUser(username, password) { success, message ->
            Log.d("MyTag", "success = $success")
            Log.d("MyTag", "message = $message")
            if (success) {
                // Login succeeded.
                //Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                // Ensure the ViewModel has the proper username.
                viewModel.name = username

                val route = Screen.HomePageScreen.withArgs(username)

                Log.d("MyTag", "Navigating to: $route")

                navController.navigate(route)


                // Navigate to the HomePageScreen and clear the back stack.
//                navController.navigate(Screen.HomePageScreen.withArgs(username)) {
//                    popUpTo(Screen.LoginScreen.route) { inclusive = true }
//                }
            } else {
                val transformedMessage = when {
                    message?.contains("401") == true -> "Wrong Password"
                    message?.contains("404") == true -> "User not Found"
                    else -> message ?: "Login failed"
                }
                // Update error state and optionally show a Toast.
                errorMessage = transformedMessage
                Toast.makeText(context, transformedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onForgotPasswordClicked() {
        navController.navigate(Screen.ForgotPasswordScreen.route)
    }
    fun onRegisterClicked() {
        navController.navigate(Screen.RegisterScreen.route)
    }

    // Main column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            painter = painterResource(R.drawable.fridge_icon_login),
            contentDescription = "Fridge Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(180.dp))

        // Username Field
        Text(text = "Username", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Your username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
        )

        // Password Field
        Text(text = "Password", style = MaterialTheme.typography.bodyMedium)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Your password") },
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

        Spacer(modifier = Modifier.height(8.dp))

        // "Forgotten your password?" Link
        TextButton(
            onClick = { onForgotPasswordClicked() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgotten your password?")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { onLoginClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Register" Link
        TextButton(
            onClick = { onRegisterClicked() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register")
        }
    }
}
