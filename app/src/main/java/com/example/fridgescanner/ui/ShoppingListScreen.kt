package com.example.fridgescanner.ui

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
import androidx.compose.material.icons.filled.Edit
import kotlin.math.sqrt








//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ShoppingListScreen(navController: NavController, viewModel: FridgeViewModel) {
//    // State to control banner visibility
//    var showBanner by remember { mutableStateOf(false) }
//    val shoppingList by viewModel.shoppingList.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Shopping List") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            // "+" button at the bottom-right corner
//            FloatingActionButton(onClick = { showBanner = !showBanner }) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Item")
//            }
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp)
//        ) {
//
//            Column(modifier = Modifier.fillMaxWidth()) {
//                Column(modifier = Modifier.fillMaxWidth()) {
//                    shoppingList.forEach { item ->
//                        Text(text = "- $item", style = MaterialTheme.typography.headlineSmall)
//                    }
//                }
//            }
//
//            // Banner for common items
//            AnimatedVisibility(
//                visible = showBanner,
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = 80.dp)  // Adjust padding as needed to position above FAB
//            ) {
//                Surface(
//                    shape = CircleShape,
//                    tonalElevation = 4.dp,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentHeight()
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(8.dp)
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//                        IconButton(onClick = {
//                            viewModel.addToShoppingList("Eggs")
//                            viewModel.fetchShoppingList()
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Egg,
//                                contentDescription = "Eggs",
//                                tint = Color.Unspecified
//                            )
//                        }
//
//                        IconButton(onClick = {
//                            viewModel.addToShoppingList("Milk")
//                            viewModel.fetchShoppingList()
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.LocalDrink,
//                                contentDescription = "Milk",
//                                tint = Color.Unspecified
//                            )
//                        }
//
//                        IconButton(onClick = {
//                            viewModel.addToShoppingList("Breakfast")
//                            viewModel.fetchShoppingList()
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.BreakfastDining,
//                                contentDescription = "Breakfast",
//                                tint = Color.Unspecified
//                            )
//                        }
//
//                        IconButton(onClick = {
//                            viewModel.addToShoppingList("Fast Food")
//                            viewModel.fetchShoppingList()
//                        }) {
//                            Icon(
//                                imageVector = Icons.Filled.Fastfood,
//                                contentDescription = "Fast Food",
//                                tint = Color.Unspecified
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(navController: NavController, viewModel: FridgeViewModel) {

    val context = LocalContext.current

    // State for banner visibility, delete confirmation, custom item dialog, and input field
    var showBanner by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val shoppingList by viewModel.shoppingList.collectAsState()
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
            showDeleteConfirmation = true
        }
    }

    // Register and unregister sensor listener using DisposableEffect
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

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(shoppingList) { item ->
                    Text(
                        text = "- $item",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            // Banner for common items plus custom item icon
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
                        IconButton(onClick = {
                            viewModel.addToShoppingList("Eggs")
                            viewModel.fetchShoppingList()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Egg,
                                contentDescription = "Eggs",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = {
                            viewModel.addToShoppingList("Milk")
                            viewModel.fetchShoppingList()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.LocalDrink,
                                contentDescription = "Milk",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = {
                            viewModel.addToShoppingList("Breakfast")
                            viewModel.fetchShoppingList()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.BreakfastDining,
                                contentDescription = "Breakfast",
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = {
                            viewModel.addToShoppingList("Fast Food")
                            viewModel.fetchShoppingList()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Fastfood,
                                contentDescription = "Fast Food",
                                tint = Color.Unspecified
                            )
                        }


                        // New IconButton for custom item
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

            // Confirmation Dialog for deleting all items
            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Delete All Items") },
                    text = { Text("Are you sure you want to delete all items in the shopping list?") },
                    confirmButton = {
                        TextButton(onClick = {
                            // Clear the shopping list
                            viewModel.clearShoppingList()
                            viewModel.fetchShoppingList()
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
                                viewModel.addToShoppingList(customItemName.trim())
                                viewModel.fetchShoppingList()
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
