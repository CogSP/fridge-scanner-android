// ManageFridgesScreen.kt
package com.example.fridgescanner.ui.fridgeui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.example.fridgescanner.R
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel

@Composable
fun ManageFridgesScreen(navController: NavController, viewModel: FridgeViewModel) {
    // Observe the list of fridges from the view model.
    val fridges by viewModel.fridges.collectAsState()
    var showCreateForm by remember { mutableStateOf(false) }
    var fridgeName by remember { mutableStateOf("") }
    var fridgeColor by remember { mutableStateOf("") } // Hex string, e.g., "#FF0000"
    val context = LocalContext.current

    // Fetch fridges when the screen is first displayed.
    LaunchedEffect(Unit) {
        viewModel.fetchFridgesForUser()
    }

    // Outer Box for overall padding and vertical centering.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center) // Centers the entire Column vertically.
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Text.
            Text(
                text = "Select a Fridge",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (fridges.isNotEmpty()) {
                // Wrap the grid in BoxWithConstraints to get available maxWidth.
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    // Define spacing and desired minimum cell width.
                    val spacing: Dp = 12.dp
                    val minCellWidth: Dp = 150.dp
                    // Calculate the number of columns that can fit.
                    val colCount = maxOf(
                        ((maxWidth + spacing) / (minCellWidth + spacing)).toInt(),
                        1
                    )
                    // Compute the width for each card.
                    val cardWidth = (maxWidth - spacing * (colCount + 2)) / colCount

                    // Group the fridges into rows (each row will have up to colCount items).
                    val rows = fridges.chunked(colCount)

                    // Use LazyColumn to list each row.
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(rows) { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                // Center the items in the row and space them by our defined spacing.
                                horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
                            ) {
                                row.forEach { fridge ->
                                    // Wrap each card in a Box with a fixed width.
                                    Box(modifier = Modifier.width(cardWidth)) {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth() // Fills the width of the Box.
                                                .aspectRatio(1f)
                                                .clickable {
                                                    viewModel.setCurrentFridgeId(fridge.id.toString())
                                                    // Navigate to the FridgeScreen.
                                                    // Here we pass a default filter ("All") via the route.
                                                    navController.navigate(Screen.FridgeScreen.withArgs("All"))
                                                },
                                            // Use a neutral card background.
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            ),
                                            shape = RoundedCornerShape(16.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                        ) {
                                            // Inside the card, display a fridge logo tinted with the fridge's color and its name.
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                // Try parsing the fridge color; fallback to primary if error.
                                                val tintColor = try {
                                                    Color(android.graphics.Color.parseColor(fridge.color))
                                                } catch (e: Exception) {
                                                    MaterialTheme.colorScheme.primary
                                                }
                                                // Display the fridge icon tinted with the fridge's color.
                                                Image(
                                                    painter = painterResource(id = R.drawable.fridge_icon_login),
                                                    contentDescription = "Fridge Icon",
                                                    modifier = Modifier.size(64.dp),
                                                    colorFilter = ColorFilter.tint(tintColor)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = fridge.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Show message when no fridges exist.
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
                        border = if (selectedColor == colorHex)
                            BorderStroke(2.dp, Color.Black)
                        else
                            BorderStroke(0.dp, Color.Transparent),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(colorHex) }
            )
        }
    }
}
