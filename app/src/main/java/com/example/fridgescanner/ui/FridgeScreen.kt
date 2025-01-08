package com.example.fridgescanner.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.example.fridgescanner.data.FridgeItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun FridgeScreen(
    navController: NavController,
    viewModel: FridgeViewModel,
    initialFilter: String = "All" // Default to "All" if not provided
) {
    val allItems by viewModel.filteredFridgeItems.collectAsState()
    val threshold by viewModel.expirationThreshold.collectAsState()

    // Separate items into categories
    val expiredItems = allItems.filter { it.isExpired() }
    val expiringSoonItems = allItems.filter { !it.isExpired() && it.isExpiringSoon(threshold) }
    val normalItems = allItems.filter { !it.isExpired() && !it.isExpiringSoon(threshold) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Selected filter state
    var selectedFilter by remember { mutableStateOf(initialFilter) }

    // Items to display based on the selected filter
    val filteredItems = when (selectedFilter) {
        "Expired" -> expiredItems.toList()
        "Expiring Soon" -> expiringSoonItems.toList()
        "Normal" -> normalItems.toList()
        else -> allItems.toList()
    }


    // -------------- Multi-select states --------------
    var multiSelectMode by remember { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<Long>() }
    // -----------------------------------------------

    // Count how many items we have
    val itemCount = allItems.size

    // ------------------- Order Dropdown -------------------
    val orderOptions = listOf("Alphabetically", "By Expiration Date")
    var expanded by remember { mutableStateOf(false) }
    var selectedOrderOption by remember { mutableStateOf(orderOptions.first()) }
    // -----------------------------------------------------------

    // We parse and sort the items after the filter is applied
    val finalItems = remember(filteredItems, selectedOrderOption) {
        when (selectedOrderOption) {
            "Alphabetically" -> filteredItems.sortedBy { it.name.lowercase() }
            "By Expiration Date" -> filteredItems.sortedBy { parseDate(it.expirationDate) }
            else -> filteredItems
        }
    }

    val bottomNavItems = listOf("Home", "Scan New Item", "Fridge", "Settings")
    var selectedBottomNav by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                selectedIndex = selectedBottomNav,
                onItemSelected = { selectedBottomNav = it },
                navController = navController,
                name = viewModel.name
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .statusBarsPadding()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header section
            FridgeHeader(
                title = if (multiSelectMode) "Select Items" else "Your Fridge Items",
                itemCount = finalItems.size,
                onRefresh = { viewModel.fetchFridgeItems() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onQueryChange = { query -> viewModel.setSearchQuery(query) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filters Row
            if (!multiSelectMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val filters = listOf("All", "Expired", "Expiring Soon", "Normal")
                    filters.forEach { filter ->
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (filter == selectedFilter) Color.Black else Color.Gray,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (filter == selectedFilter) Color(0xFFE0E0E0) else Color.Transparent
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // ------------------- Dropdown for Ordering -------------------
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Show the currently selected option
                Text(
                    text = "Order: $selectedOrderOption",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                // The dropdown button
                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text(text = "Order")
                }

                // The actual dropdown menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(x = 240.dp, y = 0.dp) //
                ) {
                    orderOptions.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order) },
                            onClick = {
                                selectedOrderOption = order
                                expanded = false
                            }
                        )
                    }
                }
            }
            // -----------------------------------------------------------

            Spacer(modifier = Modifier.height(12.dp))

            // Show error/loading/empty states
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.padding(top = 32.dp))
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    )
                }
                finalItems.isEmpty() -> {
                    Text(
                        text = "No items found.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    // Main list of items
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(finalItems) { item ->
                            // Enhanced card that supports multi-selection
                            MultiSelectFridgeItemCard(
                                item = item,
                                navController = navController,
                                multiSelectMode = multiSelectMode,
                                isSelected = selectedItems.contains(item.id),
                                onSelectItem = { selectedItem ->
                                    if (selectedItems.contains(selectedItem.id)) {
                                        selectedItems.remove(selectedItem.id)
                                    } else {
                                        selectedItems.add(selectedItem.id)
                                    }
                                    // If user deselected the last item, exit multiSelectMode if no items are left
                                    if (selectedItems.isEmpty()) {
                                        multiSelectMode = false
                                    }
                                },
                                onLongPressItem = { selectedItem ->
                                    // Enter multi-select mode if not already
                                    if (!multiSelectMode) {
                                        multiSelectMode = true
                                        selectedItems.add(selectedItem.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // If we have selected items, show a "Delete Selected" button
            if (selectedItems.isNotEmpty()) {
                Button(
                    onClick = {
                        viewModel.deleteFridgeItems(selectedItems.toList())
                        selectedItems.clear()
                        multiSelectMode = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Delete Selected (${selectedItems.size})")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

fun parseDate(dateStr: String): LocalDate? {
    return runCatching {
        LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }.getOrNull()
}

// The original FridgeHeader, SearchBar, etc. remain the same...

@Composable
fun FridgeHeader(
    title: String,
    itemCount: Int,
    onRefresh: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "$itemCount item(s) available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

//            IconButton(onClick = onRefresh) {
//                Icon(
//                    imageVector = Icons.Default.Refresh,
//                    contentDescription = "Refresh items"
//                )
//            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        label = { Text("Search Items") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiSelectFridgeItemCard(
    item: FridgeItem,
    navController: NavController,
    multiSelectMode: Boolean,
    isSelected: Boolean,
    onSelectItem: (FridgeItem) -> Unit,
    onLongPressItem: (FridgeItem) -> Unit
) {
    // We highlight or style the card differently if it's selected
    val cardColor = if (isSelected) Color(0xFFB3E5FC) else MaterialTheme.colorScheme.surfaceVariant

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (multiSelectMode) {
                        // Toggle selection
                        onSelectItem(item)
                    } else {
                        navController.navigate("fridgeItemDetail/${item.id}")
                    }
                },
                onLongClick = {
                    onLongPressItem(item)
                }
            )
            .padding(horizontal = 8.dp)
    ) {
        // Display item details, e.g. name, expiration date
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Expires on: ${item.expirationDate}",
                style = MaterialTheme.typography.bodyMedium,
            )
            // If isExpired, isExpiringSoon, or other status, you can display warnings or color
        }
    }
}
