import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.ui.BottomNavigationBar
import com.example.fridgescanner.viewmodel.FridgeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fridgescanner.Screen
import com.example.fridgescanner.ui.BottomNavigationBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController, viewModel: FridgeViewModel) {
    // Determines whether to show manual fields or just the "Scan" options
    var showManualFields by remember { mutableStateOf(false) }

    // Item states for manual entry
    var itemName by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }

    // Show DatePickerDialog state
    var showDatePicker by remember { mutableStateOf(false) }

    // If true, display the DatePickerDialog
    if (showDatePicker) {
        ShowDatePickerDialog(
            context = LocalContext.current,
            onDateSelected = { selectedDate ->
                expirationDate = selectedDate
                showDatePicker = false
            },
            onDismissRequest = { showDatePicker = false }
        )
    }

    // Bottom navigation state
    var selectedBottomNav by remember { mutableStateOf(0) }
    val bottomNavItems = listOf("Home", "Scan New Item", "Fridge", "Settings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    // If you'd like a back arrow, you can add it here (Icons.Default.ArrowBack, etc.)
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
        // Main container
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showManualFields) {

                Spacer(modifier = Modifier.height(160.dp))

                // --------- "Scan or Add" UI -----------
                Text(
                    text = "Scan a New Item",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card grouping the "Add Manually" and "Scan Barcode" buttons
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
                        Button(
                            onClick = { showManualFields = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Add Manually")
                        }

                        OutlinedButton(
                            onClick = {
                                navController.navigate(Screen.BarcodeScannerScreen.route)
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
            } else {
                // --------- Manual Entry Fields -----------
                Text(
                    text = "Add Fridge Item Manually",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )

                // Use a Card to visually separate the manual input section
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
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Item Name
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Expiration date
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Calendar Icon",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            val buttonText = if (expirationDate.isEmpty()) {
                                "Select Expiration Date"
                            } else {
                                "Expires on: $expirationDate"
                            }
                            Text(buttonText)
                        }

                        Button(
                            onClick = {
                                // 1. Validate user input
                                // 2. Possibly add to fridge (viewModel call or direct repository call)
                                // 3. Navigate back or show success
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save Item")
                        }

                        OutlinedButton(
                            onClick = { showManualFields = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Back")
                        }
                    }
                }
            }
            // Extra spacer to push UI up if there's unused space
            Spacer(modifier = Modifier.weight(1f))
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
            val formattedDate = format.format(pickedCalendar.time)

            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // If you'd like a minimum date (e.g. can't pick past dates):
    // datePicker.datePicker.minDate = calendar.timeInMillis

    LaunchedEffect(Unit) {
        datePicker.show()
    }
    DisposableEffect(Unit) {
        onDispose {
            onDismissRequest()
        }
    }
}
