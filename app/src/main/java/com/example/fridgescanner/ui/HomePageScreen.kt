package com.example.fridgescanner.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgescanner.R // Or your actual package R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.fridgescanner.Screen
import com.example.fridgescanner.data.ShoppingItem
import com.example.fridgescanner.viewmodel.FridgeViewModel
import java.time.LocalTime

@Composable
fun GreetingText(userName: String?) {
    // Get the current hour (0–23 range)
    val currentHour = LocalTime.now().hour

    val greeting = when {
        currentHour < 12 -> "Good Morning"
        currentHour < 18 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val name = if (userName.isNullOrBlank()) "Guest" else userName
    Text(
        text = "$greeting, $name",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        //color = Color(0xFF2E7D32)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(name: String?, navController: NavController, viewModel: FridgeViewModel) {
    // Example data
    val userName = name
    val services = listOf(
        ServiceItem("Shopping List", Icons.Default.ShoppingCart),
        ServiceItem("Scan Item", Icons.Default.CameraAlt),
        ServiceItem("Expired Items", Icons.Default.MoodBad),
        ServiceItem("Logout", Icons.Default.ExitToApp)
    )
    val recentTransactions = listOf(
        TransactionItem("Eggs", "Today, 10:45pm", "x2", Icons.Default.Egg),
        TransactionItem("Milk", "Today, 10:45pm", "x1", Icons.Default.LocalDrink),
        TransactionItem("Mulino Bianco Pancake", "6/2/2022, 10:45pm", "x3", Icons.Default.Fastfood)
    )

    // Bottom navigation state
    var selectedBottomNav by remember { mutableStateOf(0) }
    //val bottomNavItems = listOf("Home", "Bill Payments", "History", "Settings")
    val bottomNavItems = listOf("Home", "Scan New Item", "Fridge", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    GreetingText(userName = userName)
                },
//                actions = {
//                    IconButton(onClick = {
//                        navController.navigate(Screen.NotificationsScreen.route)
//                    }) {
//                        Icon(
//                            imageVector = Icons.Default.Notifications,
//                            contentDescription = "Notifications"
//                        )
//                    }
//                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                selectedIndex = selectedBottomNav,
                onItemSelected = { selectedBottomNav = it },
                navController = navController,
                name = name ?: "Guest"
            )
        }
    ) { innerPadding ->
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            val allItems by viewModel.filteredFridgeItems.collectAsState()
            val threshold by viewModel.expirationThreshold.collectAsState()
            val expiredItems = allItems.filter { it.isExpired() }
            val expiringSoonItems = allItems.filter { !it.isExpired() && it.isExpiringSoon(threshold) }

            ItemsStatusCard(
                totalItems = allItems.size,
                expiredCount = expiredItems.size,
                expiringSoonCount = expiringSoonItems.size,
                navController = navController,
                viewModel = viewModel
            )

            Spacer(Modifier.height(16.dp))

            // Services row
            ServicesRow(services, navController, viewModel)

            Spacer(Modifier.height(16.dp))

            // Promo card - "Season Greetings"
            PromoCard(navController = navController)

            Spacer(Modifier.height(16.dp))

            // -------------------------------
            // Shopping List Section
            // -------------------------------
            Text(
                text = "Shopping List",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Collect the shopping list from your ViewModel
            val shoppingList by viewModel.shoppingList.collectAsState()

            if (shoppingList.isNotEmpty()) {
                // You can use a Column (or LazyColumn if the list might be long)
                Column {
                    shoppingList.forEach { item ->
                        ShoppingListRow(item = item, onRemove = { itemName ->
                            viewModel.deleteItemFromShoppingList(itemName)
                        })
                    }
                }
            } else {
                Text(
                    text = "No items in your shopping list.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

fun getShoppingItemIcon(itemName: String): ImageVector {
    return when (itemName.lowercase()) {
        "milk" -> Icons.Default.LocalDrink
        "eggs" -> Icons.Default.Egg
        "pancakes" -> Icons.Default.BreakfastDining
        "burger" -> Icons.Default.Fastfood
        // Add more mappings as needed
        else -> Icons.Default.ShoppingCart // default icon
    }
}

@Composable
fun ItemsStatusCard(
    totalItems: Int,
    expiredCount: Int,
    expiringSoonCount: Int,
    navController: NavController,
    viewModel: FridgeViewModel
) {


    // Read the current fridge id from the ViewModel.
    val currentFridgeId by viewModel.currentFridgeId.collectAsState()

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Fridge Overview",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

            )
            Spacer(Modifier.height(8.dp))

            if (currentFridgeId.isNullOrEmpty() || totalItems == 0) {
                // If no fridge is selected or there are no items, show an instruction.
                Text(
                    text = "Choose a Fridge to check its product",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else {
                // Show total items
                Text(
                    text = "Total items: $totalItems",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                )

                // Show expired / expiring soon info
                Text(
                    text = "Expired: $expiredCount",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Red,
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = "Expiring Soon: $expiringSoonCount",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            Spacer(Modifier.height(8.dp))

            // Example 'Manage Items' button
            OutlinedButton(
                onClick = {
                    if (currentFridgeId.isNullOrEmpty()) {
                    // If no fridge is selected, navigate to the ManageFridgesScreen.
                    navController.navigate(Screen.ManageFridgesScreen.route)
                } else {
                    // Otherwise, navigate to the FridgeScreen (with a default filter, e.g. "All")
                    navController.navigate(Screen.FridgeScreen.withArgs("All"))
                } },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Manage Items")
            }
        }
    }
}


//---------------------------------------
// Services Row
//---------------------------------------
@Composable
fun ServicesRow(services: List<ServiceItem>, navController: NavController, viewModel: FridgeViewModel) {
    // Horizontal row of icons/text

    // State to control whether the logout confirmation dialog is shown.
    var showLogoutDialog by remember { mutableStateOf(false) }

    // If the dialog should be shown, display the AlertDialog.
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog if the user clicks outside of it or presses the back button.
                showLogoutDialog = false
            },
            title = { Text(text = "Confirm Logout") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    // When the user confirms, perform the logout:
                    viewModel.logout()
                    showLogoutDialog = false
                    navController.navigate(Screen.LoginScreen.route) {
                        // Clear the back stack to prevent navigating back to the logged-in screens.
                        popUpTo(0)
                    }
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // If the user cancels, simply dismiss the dialog.
                    showLogoutDialog = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }


    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        services.forEach { service ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Circle icon background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x17F44336)),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        if (service.name == "Shopping List") {
                            navController.navigate(Screen.ShoppingListScreen.route)
                        }
                        if (service.name == "Scan Item") {
                            navController.navigate(Screen.BarcodeScannerScreen.route)
                        }
                        if (service.name == "Logout") {
                            showLogoutDialog = true
                        }
                        if (service.name == "Expired Items") {
                            navController.navigate(Screen.FridgeScreen.route + "/Expired")
                        }
                    }) {
                        Icon(
                            imageVector = service.icon,
                            contentDescription = service.name,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = service.name,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

//---------------------------------------
// Promo Card
//---------------------------------------
@Composable
fun PromoCard(navController: NavController) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            // Make the whole card clickable
            .clickable {
                navController.navigate(Screen.ProPromoScreen.route)
            },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Buy Fridge Scanner Pro",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "40% off only for today!",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // A small image or icon
            Icon(
                imageVector = Icons.Default.Egg,
                contentDescription = "Egg",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}



//---------------------------------------
// Recent Transactions
//---------------------------------------
@Composable
fun TransactionRow(item: TransactionItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Icon or company logo
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color(0xFF9C27B0),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(text = item.title, fontWeight = FontWeight.SemiBold)
                Text(text = item.subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        // The negative amount
        Text(
            text = item.amount,
            color = Color.Red,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

//---------------------------------------
// Shopping List Row
//---------------------------------------
@Composable
fun ShoppingListRow(item: ShoppingItem, onRemove: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = getShoppingItemIcon(item.name),
                contentDescription = item.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (item.quantity > 1) "${item.name} (${item.quantity})" else item.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
        IconButton(onClick = { onRemove(item.name) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remove ${item.name}",
                tint = Color.Red
            )
        }
    }
}


//---------------------------------------
// Bottom Navigation
//---------------------------------------
@Composable
fun BottomNavigationBar(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    navController: NavController,
    name: String
) {

    val currentRoute = currentRoute(navController)

    // We wrap the NavigationBar in a Column,
    // with a Divider on top for the horizontal line.
    Column {
        // The horizontal line on top:
        Divider(
            color = MaterialTheme.colorScheme.outlineVariant, // or colorScheme.onSurface.copy(alpha=0.12f), etc.
            thickness = 1.dp
        )

        // The actual Material 3 NavigationBar
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            items.forEachIndexed { index, label ->
                // Choose icons by label or fallback
                val icon = when (label) {
                    "Scan New Item" -> Icons.Default.CameraAlt
                    "Fridge" -> Icons.Default.Kitchen
                    "Home" -> Icons.Default.Home
                    "Settings" -> Icons.Default.Settings
                    else -> Icons.Default.Circle
                }

                val isSelected = currentRoute == getRouteForLabel(label, name)

                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label, fontSize = 12.sp) },
                    selected = isSelected,
                    onClick = {
                        onItemSelected(index)
                        navigateToRoute(navController, label, name)
                    },
                    alwaysShowLabel = true
                )
            }
        }
    }
}


@Composable
private fun currentRoute(navController: NavController): String? {
    val navbackStackEntry by navController.currentBackStackEntryAsState()
    return navbackStackEntry?.destination?.route
}


private fun getRouteForLabel(label: String, name: String): String {
    return when (label) {
        "Home" -> Screen.HomePageScreen.withArgs(name)
        "Scan New Item" -> Screen.ScanScreen.route
        "Fridge" -> Screen.FridgeScreen.route + "/All"
        "Settings" -> Screen.OptionsScreen.route
        else -> ""
    }
}


private fun navigateToRoute(navController: NavController, label: String, name: String) {
    navController.navigate(getRouteForLabel(label, name))
}


//---------------------------------------
// Data Classes
//---------------------------------------
data class ServiceItem(val name: String, val icon: ImageVector)
data class TransactionItem(val title: String, val subtitle: String, val amount: String, val icon: ImageVector)
