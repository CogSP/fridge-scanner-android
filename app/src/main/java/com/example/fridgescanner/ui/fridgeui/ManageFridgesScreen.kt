// ManageFridgesScreen.kt
package com.example.fridgescanner.ui.fridgeui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel

@Composable
fun ManageFridgesScreen(navController: NavController, viewModel: FridgeViewModel) {
    val fridges by viewModel.fridges.collectAsState()
    var showCreateForm by remember { mutableStateOf(false) }
    var fridgeName by remember { mutableStateOf("") }
    var fridgeColor by remember { mutableStateOf("") } // Hex string, e.g., "#FF0000"
    val context = LocalContext.current

    // Fetch fridges when the screen is first displayed.
    LaunchedEffect(Unit) {
        viewModel.fetchFridgesForUser()
    }

    // Wrap everything in a Box so the content is centered vertically.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Text
            Text(
                text = "Select a Fridge",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            // If there are fridges, show them in a scrollable list with a max height.
            if (fridges.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                ) {
                    items(fridges) { fridge ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Navigate to the FridgeScreen for this fridge.
                                    navController.navigate(Screen.FridgeScreen.withArgs(fridge.id.toString()))
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = try {
                                    Color(android.graphics.Color.parseColor(fridge.color))
                                } catch (e: Exception) {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = fridge.name,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // If no fridges exist, show a centered message.
                Text(
                    text = "No fridges available.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { showCreateForm = true }) {
                Text("Create New Fridge")
            }

            if (showCreateForm) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Create a New Fridge",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = fridgeName,
                            onValueChange = { fridgeName = it },
                            label = { Text("Fridge Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pick a Color",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorPicker(selectedColor = fridgeColor, onColorSelected = { fridgeColor = it })
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = {
                                viewModel.createFridge(fridgeName, fridgeColor) { success, message ->
                                    if (success) {
                                        Toast.makeText(context, "Fridge created", Toast.LENGTH_SHORT).show()
                                        showCreateForm = false
                                        fridgeName = ""
                                        fridgeColor = ""
                                    } else {
                                        Toast.makeText(context, message ?: "Creation failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }) {
                                Text("Create")
                            }
                            OutlinedButton(onClick = { showCreateForm = false }) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPicker(selectedColor: String, onColorSelected: (String) -> Unit) {
    // A list of sample color hex codes.
    val colors = listOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
        "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50",
        "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800",
        "#FF5722", "#795548", "#9E9E9E", "#607D8B"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(colors) { colorHex ->
            val color = try {
                Color(android.graphics.Color.parseColor(colorHex))
            } catch (e: Exception) {
                MaterialTheme.colorScheme.primary
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        border = if (selectedColor == colorHex) BorderStroke(2.dp, Color.Black) else BorderStroke(0.dp, Color.Transparent),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorHex) }
            )
        }
    }
}
