package com.example.fridgescanner.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen


@Composable
fun MainScreen(navController: NavController) {
    var text by remember {
        mutableStateOf("")
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally, // Center horizontally
        modifier = Modifier
            .fillMaxSize() // Fill the entire screen
            .padding(horizontal = 32.dp) // Adjust padding as needed
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter your name") }, // Optional: Add a label for better UX
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp) // Optional: Adjust padding inside TextField
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate(Screen.DetailScreen.withArgs(text))
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Center the button horizontally
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Login")
        }
    }
}

