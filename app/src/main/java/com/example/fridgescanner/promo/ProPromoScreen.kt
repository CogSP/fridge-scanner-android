package com.example.fridgescanner.promo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProPromoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fridge Scanner Pro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Main content
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            // Optional illustration or promo image
            // E.g., if you have a local drawable: painterResource(R.drawable.your_promo_img)
            // or a placeholder from the internet
            // If you have no image, remove this section
//            Image(
//                painter = painterResource(R.drawable.ic_fridge_placeholder),
//                contentDescription = "Pro Illustration",
//                modifier = Modifier
//                    .size(180.dp)
//            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Unlock the Full Power!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Upgrade to Fridge Scanner Pro",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(20.dp))

            // A Card or Container for bullet points
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Features & Benefits:", style = MaterialTheme.typography.titleMedium)

                    BulletPoint(text = "Unlimited items in your fridge")
                    BulletPoint(text = "Advanced notifications and reminders")
                    BulletPoint(text = "Exclusive discount tracking for groceries")
                    BulletPoint(text = "Priority customer support 24/7")
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Grab 40% off — Limited Time!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.height(24.dp))

            // "Proceed to Payment" dummy button
            Button(
                onClick = {
                    // This is a dummy for now, do nothing or show a toast/snackbar
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Proceed to Payment",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•  ",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
