package com.example.fridgescanner.ui.scanitems

import android.app.DatePickerDialog
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.Screen
import com.example.fridgescanner.ui.BottomNavigationBar
import com.example.fridgescanner.util.ToastHelper
import com.example.fridgescanner.viewmodel.FridgeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController, viewModel: FridgeViewModel) {
    val context = LocalContext.current

    // We'll use a single variable to hold the expiration date.
    var selectedExpiryDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    // When true, display the DatePickerDialog.
    if (showDatePicker) {
        ShowDatePickerDialog(
            context = context,
            onDateSelected = { date ->
                selectedExpiryDate = date
                showDatePicker = false
                // store the chosen date in the ViewModel if needed:
                viewModel.setSelectedExpiryDate(date)
            },
            onDismissRequest = { showDatePicker = false }
        )
    }

    // Bottom navigation state.
    var selectedBottomNav by remember { mutableStateOf(0) }
    val bottomNavItems = listOf("Home", "Scan New Item", "Fridge", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    // Optionally add a back arrow.
                }
            )
        },
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(160.dp))

            // --------- "Scan or Add" UI -----------
            Text(
                text = "Scan a New Item",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x00FFFFFF)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Button for selecting an expiration date.
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (selectedExpiryDate.isEmpty()) "Select Expiration Date" else "Expires on: $selectedExpiryDate"
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            // Ensure an expiration date is selected before scanning.
                            if (selectedExpiryDate.isEmpty()) {
                                ToastHelper.showToast(
                                    context,
                                    "Please select an expiration date first."
                                )
                            } else {
                                // Navigate to your barcode scanner screen.
                                // In your barcode scanner logic, make sure to pass along the selected expiry date.
                                navController.navigate(Screen.BarcodeScannerScreen.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Icon",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan Barcode")
                    }
                }
            }
        }
    }
}


@Composable
fun ShowDatePickerDialog(
    context: Context,
    onDateSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val pickedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            onDateSelected(format.format(pickedCalendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(Unit) {
        datePicker.show()
    }
    DisposableEffect(Unit) {
        onDispose {
            onDismissRequest()
        }
    }
}
