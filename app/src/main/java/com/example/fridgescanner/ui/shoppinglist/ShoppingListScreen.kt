package com.example.fridgescanner.ui.shoppinglist

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.viewmodel.FridgeViewModel







// this should be placed in another class
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import kotlin.math.sqrt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavController, viewModel: FridgeViewModel) {
    val context = LocalContext.current

    // State for banner visibility, delete confirmation, custom item dialog, and input field
    var showBanner by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val shoppingList by viewModel.shoppingList.collectAsState()

    // Additional states for completing/removing items
    val completedItems = remember { mutableStateListOf<String>() }

    // Dialog for adding custom items
    var showCustomItemDialog by remember { mutableStateOf(false) }
    var customItemName by remember { mutableStateOf("") }

    // Initialize SensorManager and ShakeDetector
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val accelerometer = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    val shakeDetector = remember {
        ShakeDetector {
            // On shake, ask if we want to delete **all** items
            showDeleteConfirmation = true
        }
    }


    // Register and unregister sensor listener
    DisposableEffect(sensorManager, accelerometer) {
        accelerometer?.also { sensor ->
            sensorManager.registerListener(shakeDetector, sensor, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose {
            sensorManager.unregisterListener(shakeDetector)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopping List") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showBanner = !showBanner }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Main list of shopping items
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(shoppingList) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // A checkbox to toggle item completion
                        Checkbox(
                            checked = completedItems.contains(item.name),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    completedItems.add(item.name)
                                } else {
                                    completedItems.remove(item.name)
                                }
                            }
                        )

                        // Display "Eggs (3)" if quantity is 3, for example
                        Text(
                            text = if (item.quantity > 1) "${item.name} (${item.quantity})" else item.name,
                            style = if (completedItems.contains(item.name))
                                MaterialTheme.typography.titleMedium.copy(color = Color.Gray)
                            else
                                MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f) // occupy remaining space
                        )

                        // Remove icon
                        IconButton(
                            onClick = {
                                viewModel.removeShoppingItem(item.name)
                                // Also remove from completedItems if present
                                completedItems.remove(item.name)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Item",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            // Banner for pre-filled items + custom item
            AnimatedVisibility(
                visible = showBanner,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Each icon calls addOrIncrementShoppingItem
                        IconButton(onClick = {
                            viewModel.addOrIncrementShoppingItem("Eggs")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Egg,
                                contentDescription = "Eggs",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = {
                            viewModel.addOrIncrementShoppingItem("Milk")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.LocalDrink,
                                contentDescription = "Milk",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = {
                            viewModel.addOrIncrementShoppingItem("Breakfast")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.BreakfastDining,
                                contentDescription = "Breakfast",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = {
                            viewModel.addOrIncrementShoppingItem("Fast Food")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Fastfood,
                                contentDescription = "Fast Food",
                                tint = Color.Unspecified
                            )
                        }
                        // Custom item entry
                        IconButton(onClick = {
                            showCustomItemDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Custom Item",
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }

            // Confirmation Dialog for deleting ALL items on shake
            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Delete All Items") },
                    text = {
                        Text("Are you sure you want to delete all items in the shopping list?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.clearShoppingList()
                            completedItems.clear()
                            showDeleteConfirmation = false
                        }) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text("No")
                        }
                    }
                )
            }

            // Dialog for adding a custom item
            if (showCustomItemDialog) {
                AlertDialog(
                    onDismissRequest = { showCustomItemDialog = false },
                    title = { Text("Add Custom Item") },
                    text = {
                        OutlinedTextField(
                            value = customItemName,
                            onValueChange = { customItemName = it },
                            label = { Text("Item name") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (customItemName.isNotBlank()) {
                                viewModel.addOrIncrementShoppingItem(customItemName.trim())
                            }
                            customItemName = ""
                            showCustomItemDialog = false
                        }) {
                            Text("Add")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            customItemName = ""
                            showCustomItemDialog = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}



// this should be placed in another class
class ShakeDetector(private val onShake: () -> Unit) : SensorEventListener {
    // Threshold for shake detection (m/s^2)
    private val shakeThreshold = 12f
    // Time interval to reset shake detection
    private val shakeResetTime = 1000L
    private var lastShakeTime = 0L

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]
                val gForce = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

                if (gForce > shakeThreshold) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastShakeTime > shakeResetTime) {
                        lastShakeTime = currentTime
                        onShake()
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
