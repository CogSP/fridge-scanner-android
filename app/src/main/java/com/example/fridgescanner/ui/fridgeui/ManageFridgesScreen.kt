// ManageFridgesScreen.kt
package com.example.fridgescanner.ui.fridgeui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ManageFridgesScreen(navController: NavController, viewModel: FridgeViewModel) {
    // Observe the list of fridges from the view model.
    val fridges by viewModel.fridges.collectAsState()
    var showCreateForm by remember { mutableStateOf(false) }
    var fridgeName by remember { mutableStateOf("") }
    var fridgeColor by remember { mutableStateOf("") } // Hex string, e.g
    val context = LocalContext.current

    // Multi-select state for deletion.
    var multiSelectMode by remember { mutableStateOf(false) }
    val selectedFridgeIds = remember { mutableStateListOf<String>() }

    // Fetch fridges when the screen is first displayed.
    LaunchedEffect(Unit) {
        Log.d("ManageFridgesScreen", "Fetching fridges...")
        viewModel.fetchFridgesForUser()
        Log.d("ManageFridgesScreen", "Fetched fridges")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select a Fridge") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
                Spacer(modifier = Modifier.height(24.dp))
                if (fridges.isNotEmpty()) {
                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val spacing: Dp = 12.dp
                        val minCellWidth: Dp = 150.dp
                        val colCount = maxOf(((maxWidth + spacing) / (minCellWidth + spacing)).toInt(), 1)
                        val cardWidth = (maxWidth - spacing * (colCount + 2)) / colCount
                        val rows = fridges.chunked(colCount)

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(spacing),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(rows) { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
                                ) {
                                    row.forEach { fridge ->
                                        // Use combinedClickable to handle onClick and onLongClick.
                                        Box(
                                            modifier = Modifier
                                                .width(cardWidth)
                                                .combinedClickable(
                                                    onClick = {
                                                        if (multiSelectMode) {
                                                            // Toggle selection
                                                            val fridgeId = fridge.id.toString()
                                                            if (selectedFridgeIds.contains(fridgeId)) {
                                                                selectedFridgeIds.remove(fridgeId)
                                                            } else {
                                                                selectedFridgeIds.add(fridgeId)
                                                            }
                                                            // If no fridge is selected, exit multi-select mode
                                                            if (selectedFridgeIds.isEmpty()) {
                                                                multiSelectMode = false
                                                            }
                                                        } else {
                                                            // Normal behavior: navigate to fridge screen.
                                                            viewModel.setCurrentFridgeId(fridge.id.toString())
                                                            navController.navigate(
                                                                Screen.FridgeScreen.withArgs("All")
                                                            )
                                                        }
                                                    },
                                                    onLongClick = {
                                                        // Enter multi-select mode if not already active.
                                                        if (!multiSelectMode) {
                                                            multiSelectMode = true
                                                            selectedFridgeIds.add(fridge.id.toString())
                                                        }
                                                    }
                                                )
                                        ) {
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(1f)
                                                    .border(
                                                        // If selected, draw a border to indicate selection.
                                                        border = if (selectedFridgeIds.contains(fridge.id.toString()))
                                                            BorderStroke(2.dp, Color.Red)
                                                        else
                                                            BorderStroke(0.dp, Color.Transparent),
                                                        shape = RoundedCornerShape(16.dp)
                                                    ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surface
                                                ),
                                                shape = RoundedCornerShape(16.dp),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(8.dp),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    val tintColor = try {
                                                        Color(android.graphics.Color.parseColor(fridge.color))
                                                    } catch (e: Exception) {
                                                        MaterialTheme.colorScheme.primary
                                                    }
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
                            ColorPicker(
                                selectedColor = fridgeColor,
                                onColorSelected = { fridgeColor = it })
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(onClick = {
                                    viewModel.createFridge(
                                        fridgeName,
                                        fridgeColor
                                    ) { success, message, newFridgeId ->
                                        if (success && newFridgeId != null) {
                                            Toast.makeText(
                                                context,
                                                "Fridge created",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            showCreateForm = false
                                            fridgeName = ""
                                            fridgeColor = ""

                                            //viewModel.setCurrentFridgeId(newFridgeId)

                                            //navController.navigate(Screen.FridgeScreen.withArgs("All"))

                                        } else {
                                            Toast.makeText(
                                                context,
                                                message ?: "Creation failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
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

                // Show Delete Selected button when in multi-select mode.
                if (multiSelectMode && selectedFridgeIds.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            // Call the ViewModel function to delete the selected fridges.
                            viewModel.deleteFridges(selectedFridgeIds) { success, message ->
                                if (success) {
                                    Toast.makeText(context, "Fridges deleted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, message ?: "Deletion failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                            selectedFridgeIds.clear()
                            multiSelectMode = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text("Delete Selected (${selectedFridgeIds.size})")
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
