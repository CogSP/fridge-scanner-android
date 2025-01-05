package com.example.fridgescanner.ui

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
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.R
import com.example.fridgescanner.Screen


//@Composable
//fun MainScreen(navController: NavController) {
//    var text by remember {
//        mutableStateOf("")
//    }
//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
//        modifier = Modifier
//            .fillMaxSize() // Fill the entire screen
//            .padding(horizontal = 32.dp) // Adjust padding as needed
//    ) {
//        TextField(
//            value = text,
//            onValueChange = { text = it },
//            label = { Text("Enter your name") }, // Optional: Add a label for better UX
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp) // Optional: Adjust padding inside TextField
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(
//            onClick = {
//                navController.navigate(Screen.DetailScreen.withArgs(text))
//            },
//            modifier = Modifier
//                .align(Alignment.CenterHorizontally) // Center the button horizontally
//                .padding(horizontal = 16.dp)
//        ) {
//            Text(text = "Login")
//        }
//    }
//}




@Composable
fun MainScreen(navController: NavController) {
    // State for user input
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Whether the password is visible or masked
    var passwordVisible by remember { mutableStateOf(false) }

    // For demonstration, we’ll do a no-op click for these actions
    fun onLoginClicked() { /* TODO: Handle login */ }
    fun onForgotPasswordClicked() { /* TODO: Handle password reset */ }
    fun onRegisterClicked() { /* TODO: Handle register navigation */ }

    // Main column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            painter = painterResource(R.drawable.fridge_icon_login),
            contentDescription = "Fridge Logo",
            modifier = Modifier.size(120.dp), // example size
            //tint = MaterialTheme.colorScheme.primary
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
            modifier = Modifier.align(Alignment.End) // right-align
        ) {
            Text("Forgotten your password?")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = { navController.navigate(Screen.HomePageScreen.withArgs(username)) }, //TODO: to substitute with onLoginClicked()
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp) // slightly rounded corners
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Register" link
        TextButton(
            onClick = { onRegisterClicked() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Register")
        }
    }
}
