package com.example.fridgescanner.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.viewmodel.FridgeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeItemDetailScreen(itemId: Int, navController: NavController, viewModel: FridgeViewModel) {

    LaunchedEffect(key1 = itemId) {
        viewModel.fetchFridgeItemById(itemId)
    }

    val item by viewModel.fridgeItemDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
                            imageVector = Icons.Filled.ArrowBack,
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

                    val icon = itemIcons[item!!.name.lowercase()] ?: Icons.Filled.ShoppingCart

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        item { // Display the icon at the top
                            Icon(
                                imageVector = icon,
                                contentDescription = "Item Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        item {
                            Text(
                                text = item!!.name,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item { // General Info Section
                            ExpandableSection(title = "General Info", defaultExpanded = true) {
                                Text(
                                    text = "Name: ${item!!.name}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Brand: ${item!!.brand}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Category: ${item!!.category}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Quantity: ${item!!.quantity}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Expiration Date: ${item!!.expirationDate}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }

                        item { // Nutriments Section
                            ExpandableSection(title = "Nutriments (per 100g)", defaultExpanded = false) {
                                Text(
                                    text = "Carbohydrates: ${item!!.carbohydrates100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Energy: ${item!!.energyKcal100g} kcal",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Fat: ${item!!.fat100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Fiber: ${item!!.fiber100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Proteins: ${item!!.proteins100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Salt: ${item!!.salt100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Saturated Fat: ${item!!.saturatedFat100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Sodium: ${item!!.sodium100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Sugars: ${item!!.sugars100g}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }


                        item { // Additional Info Section
                            ExpandableSection(title = "Additional Info", defaultExpanded = false) {
                                Text(
                                    text = "Allergens: ${item!!.allergens}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Conservation Conditions: ${item!!.conservationConditions}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Countries Where Sold: ${item!!.countriesWhereSold}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Owner: ${item!!.ownerImported}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Preparation: ${item!!.preparation}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
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

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
        //elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                //.padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
fun ExpandableSection(title: String, defaultExpanded: Boolean, content: @Composable ColumnScope.() -> Unit) {
    var isExpanded by remember { mutableStateOf(defaultExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
                //.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = "Expand/Collapse"
            )
        }
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                content()
            }
        }
    }
}