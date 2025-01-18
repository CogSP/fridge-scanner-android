package com.example.fridgescanner.ui.fridgeui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.data.FridgeItem

@Composable
fun FridgeItemCard(item: FridgeItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("fridgeItemDetail/${item.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Add elevation
        shape = RoundedCornerShape(8.dp) // Use a rounded corner shape
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val itemIcons = mapOf(
                "eggs" to Icons.Filled.Egg, // Replace with actual egg icon
                "milk" to Icons.Filled.LocalDrink, // Replace with actual milk icon
                "butter" to Icons.Filled.BreakfastDining,
                "bread" to Icons.Filled.BreakfastDining
                // ... add more mappings for other items
            )
            val icon = itemIcons[item.name.lowercase()] ?: Icons.Filled.ShoppingCart // Default icon
            Icon(
                imageVector = icon, // Replace with your icon
                contentDescription = "Fridge item",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp)) // Add spacing between icon and text
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold // Make the name bold
                )
                Text(
                    text = "Expires on: ${item.expirationDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray // Use a different color for the expiration date
                )
            }
        }
    }
}