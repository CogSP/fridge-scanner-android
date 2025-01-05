package com.example.fridgescanner.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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


@Composable
fun HomePageScreen(name: String?, navController: NavController, viewModel: FridgeViewModel) {
    // Example data
    val userName = name
    val walletBalance = "₦2,440.30"
    val services = listOf(
        ServiceItem("Top up", Icons.Default.Phone),
        ServiceItem("Electricity", Icons.Default.Bolt),
        ServiceItem("TV", Icons.Default.Tv),
        ServiceItem("Education", Icons.Default.School)
    )
    val recentTransactions = listOf(
        TransactionItem("Eko Electrical", "Today, 10:45pm", "-80,000", Icons.Default.ElectricalServices),
        TransactionItem("DSTV Premium", "Today, 10:45pm", "-80,000", Icons.Default.Tv),
        TransactionItem("MTN Airtime Topup", "6/2/2022, 10:45pm", "-80,000", Icons.Default.Phone)
    )

    // Bottom navigation state
    var selectedBottomNav by remember { mutableStateOf(0) }
    //val bottomNavItems = listOf("Home", "Bill Payments", "History", "Settings")
    val bottomNavItems = listOf("Scan New Item", "Home", "Settings")

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                selectedIndex = selectedBottomNav,
                onItemSelected = { selectedBottomNav = it }
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

            GreetingText(userName = userName)

            Spacer(Modifier.height(16.dp))

            // Wallet balance card
            //WalletBalanceCard(balance = walletBalance)

            val allItems by viewModel.filteredFridgeItems.collectAsState()
            val threshold by viewModel.expirationThreshold.collectAsState()
            val expiredItems = allItems.filter { it.isExpired() }
            val expiringSoonItems = allItems.filter { !it.isExpired() && it.isExpiringSoon(threshold) }

            ItemsStatusCard(
                totalItems = allItems.size,
                expiredCount = expiredItems.size,
                expiringSoonCount = expiringSoonItems.size
            )

            Spacer(Modifier.height(16.dp))

            // Services row
            ServicesRow(services)

            Spacer(Modifier.height(16.dp))

            // Promo card - "Season Greetings"
            PromoCard()

            Spacer(Modifier.height(16.dp))

            // Recent transactions
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column {
                recentTransactions.forEach {
                    TransactionRow(it)
                }
            }
        }
    }
}


@Composable
fun ItemsStatusCard(
    totalItems: Int,
    expiredCount: Int,
    expiringSoonCount: Int
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        //colors = CardDefaults.cardColors(containerColor = Color(0xFF212121)) // Dark-ish background
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
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f))
            )
            Spacer(Modifier.height(4.dp))

            // Show total items
            Text(
                text = "Total items: $totalItems",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(Modifier.height(8.dp))

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
                    color = Color.Yellow,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(Modifier.height(8.dp))

            // Example 'Manage Items' button
            OutlinedButton(
                onClick = { /* Navigate to your FridgeScreen or a Manage Items screen */ },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                //border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(colors = listOf(Color(0xFF2E7D32), Color(0xFF2E7D32)))),
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
fun ServicesRow(services: List<ServiceItem>) {
    // Horizontal row of icons/text
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
                    Icon(
                        imageVector = service.icon,
                        contentDescription = service.name,
                        tint = Color(0xFF673AB7),
                        modifier = Modifier.size(24.dp)
                    )
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
fun PromoCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        //colors = CardDefaults.cardColors(containerColor = Color(0xFFDFFFD6)) // light greenish
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
                    text = "Qualcosa legato agli sconti",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    //color = Color(0xFF2E7D32)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Enjoy 40% off all purchase",
                    style = MaterialTheme.typography.bodyMedium,
                    //color = Color(0xFF2E7D32)
                )
            }

            // If you have an image resource for the egg, show it here:
            // Example using a placeholder icon or image
            Icon(
                imageVector = Icons.Default.Egg,
                contentDescription = "Egg",
                //tint = Color(0xFF2E7D32),
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
// Bottom Navigation
//---------------------------------------
@Composable
fun BottomNavigationBar(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    // We wrap the NavigationBar in a Column,
    // with a Divider on top for the horizontal line.
    Column {
        // The horizontal line on top:
        Divider(
            color = Color(0xFFE0E0E0), // a light gray
            thickness = 1.dp
        )

        // The actual Material 3 NavigationBar
        NavigationBar(
            containerColor = Color.White
        ) {
            items.forEachIndexed { index, label ->
                // Choose icons by label or fallback
                val icon = when (label) {
                    "Scan New Item" -> Icons.Default.CameraAlt
                    //"Bill Payments" -> Icons.Default.Receipt
                    "Home" -> Icons.Default.Home
                    "Settings" -> Icons.Default.Settings
                    else -> Icons.Default.Circle
                }
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label, fontSize = 12.sp) },
                    selected = (index == selectedIndex),
                    onClick = { onItemSelected(index) },
                    alwaysShowLabel = true
                )
            }
        }
    }
}


//---------------------------------------
// Data Classes
//---------------------------------------
data class ServiceItem(val name: String, val icon: ImageVector)
data class TransactionItem(val title: String, val subtitle: String, val amount: String, val icon: ImageVector)
