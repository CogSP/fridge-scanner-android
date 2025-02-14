package com.example.fridgescanner.ui.fridgeui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.fridgescanner.R
import com.example.fridgescanner.Screen
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.ui.BottomNavigationBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen(
    navController: NavController,
    viewModel: FridgeViewModel,
    initialFilter: String = "All" // Default to "All" if not provided
) {
    val allItems by viewModel.filteredFridgeItems.collectAsState()
    val threshold by viewModel.expirationThreshold.collectAsState()

    // Read the current fridge id from the ViewModel.
    val currentFridgeId by viewModel.currentFridgeId.collectAsState()

    // If no fridge is currently selected, navigate back to ManageFridgesScreen.
//    LaunchedEffect(currentFridgeId) {
//        if (currentFridgeId == null) {
//            navController.navigate(Screen.ManageFridgesScreen.route)
//        }
//    }

    // If no fridge is selected, navigate away immediately and do not render UI.
    if (currentFridgeId == null) {
        // Use LaunchedEffect to perform a side-effect (navigation) once.
        LaunchedEffect(Unit) {
            navController.navigate(Screen.ManageFridgesScreen.route) {
                popUpTo(Screen.FridgeScreen.route) { inclusive = true }
            }
        }
        // Return early so that nothing is rendered.
        return
    } else {
        LaunchedEffect(currentFridgeId) {
            viewModel.fetchFridgeItemsForCurrentFridge()
        }
    }

    // State to control the "Change Fridge" confirmation dialog.
    var showChangeFridgeDialog by remember { mutableStateOf(false) }
    // State to control the "Share Fridge" popup.
    var showSharePopup by remember { mutableStateOf(false) }
    // State for the email input in the share popup.
    var shareEmail by remember { mutableStateOf("") }
    // State for the list of already shared users.
    val sharedUsers by viewModel.sharedUsers.collectAsState()

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
            "By Expiration Date" -> filteredItems.sortedBy { it.expiry_date }
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
                onChangeFridgeClicked = { showChangeFridgeDialog = true },
                onShareClicked = {
                    viewModel.fetchFridgeMembers(currentFridgeId!!)
                    showSharePopup = true
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

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
                                },
                                onRemoveItem = { }
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

    // Confirmation dialog for changing fridge.
    if (showChangeFridgeDialog) {
        AlertDialog(
            onDismissRequest = { showChangeFridgeDialog = false },
            title = { Text("Change Fridge") },
            text = { Text("Do you want to select a different fridge?") },
            confirmButton = {
                TextButton(onClick = {
                    // Clear the current fridge selection and navigate back.
                    viewModel.clearCurrentFridgeId()
                    navController.navigate(Screen.ManageFridgesScreen.route) {
                        popUpTo(Screen.FridgeScreen.route) { inclusive = true }
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeFridgeDialog = false }) {
                    Text("No")
                }
            }
        )
    }


    // Then, in your AlertDialog for sharing:
    if (showSharePopup) {
        // Assume the owner’s email is available via viewModel.ownerEmail (or another property)
        val ownerEmail by viewModel.ownerEmail.collectAsState(initial = "owner@example.com")
        val ownerName by viewModel.ownerName.collectAsState(initial = "Owner Name")

        AlertDialog(
            onDismissRequest = { showSharePopup = false },
            title = { Text("Share Fridge") },
            text = {
                Column {
                    OutlinedTextField(
                        value = shareEmail,
                        onValueChange = { shareEmail = it },
                        label = { Text("Enter user's email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fridge access details:", style = MaterialTheme.typography.titleMedium)
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "Owner Name",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        ownerName?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Owner Email",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        ownerEmail?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Also shared with:", style = MaterialTheme.typography.titleMedium)
                    if (sharedUsers.isEmpty()) {
                        Text("No additional users.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        LazyColumn {
                            items(sharedUsers) { userEmail ->
                                Text(text = userEmail, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (shareEmail.isNotBlank()) {
                            val fridgeId = viewModel.currentFridgeId.value
                            if (fridgeId != null) {
                                viewModel.shareFridge(
                                    shareEmail,
                                    fridgeId
                                ) { success, responseMessage ->
                                    if (success) {
                                        // After a successful share, refresh the list from the backend.
                                        viewModel.fetchFridgeMembers(fridgeId)
                                        shareEmail = ""
                                        showSharePopup = false
                                    } else {
                                        Log.e(
                                            "FridgeScreen",
                                            "Error sharing fridge: $responseMessage"
                                        )
                                    }
                                }
                            } else {
                                Log.e("FridgeScreen", "No fridge selected.")
                            }
                        }
                    }
                ) {
                    Text("Share")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSharePopup = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
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
    onChangeFridgeClicked: () -> Unit,
    onShareClicked: () -> Unit
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
            // "Share Fridge" plus button.
            IconButton(onClick = onShareClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Share Fridge",
                    modifier = Modifier.size(36.dp)
                )
            }
            // Use your fridge icon instead of the edit icon.
            IconButton(onClick = { onChangeFridgeClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.fridge_icon_login),
                    contentDescription = "Change Fridge",
                    modifier = Modifier.size(36.dp)
                )
            }
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
    onLongPressItem: (FridgeItem) -> Unit,
    onRemoveItem: (FridgeItem) -> Unit
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
                text = "Expires on: ${item.expiry_date}",
                style = MaterialTheme.typography.bodyMedium,
            )
            // If isExpired, isExpiringSoon, or other status, you can display warnings or color
        }
    }
}
