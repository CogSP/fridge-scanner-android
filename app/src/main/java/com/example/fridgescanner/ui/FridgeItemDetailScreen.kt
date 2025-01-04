package com.example.fridgescanner.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.viewmodel.FridgeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeItemDetailScreen(itemId: Int, navController: NavController, viewModel: FridgeViewModel) {
    // Fetch the item details
    LaunchedEffect(key1 = itemId) {
        viewModel.fetchFridgeItemById(itemId)
    }

    // Collect state from ViewModel
    val item by viewModel.fridgeItemDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Map from item names (lowercase) to icons
    // Adjust or expand as needed for your products
    val itemIcons = mapOf(
        "milk" to Icons.Filled.LocalDrink,
        "eggs" to Icons.Filled.Egg,
        "butter" to Icons.Filled.BreakfastDining
        // Add more items as needed
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Replace with a back icon if desired
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                item != null -> {
                    // Determine the icon to show based on item name
                    val icon = itemIcons[item!!.name.lowercase()] ?: Icons.Filled.ShoppingCart
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {

                        // Display the icon at the top
                        Icon(
                            imageVector = icon,
                            contentDescription = "Item Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = item!!.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Quantity: ${item!!.quantity}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Expiration Date: ${item!!.expirationDate}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        // Add more detailed information as needed
                    }
                }
                else -> {
                    Text(
                        text = "No item details available.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
