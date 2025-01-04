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
import com.example.fridgescanner.viewmodel.FridgeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController, viewModel: FridgeViewModel) {
    // We'll keep track of whether the user wants to add manually or scan
    // If you want separate screens for each approach, you can navigate to them.
    // Alternatively, you can conditionally show UI in the same screen.
    var showManualFields by remember { mutableStateOf(false) }

    // We'll also track user input for item name, expiration date, etc., if adding manually
    var itemName by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }

    // DatePickerDialog requires context
    val context = androidx.compose.ui.platform.LocalContext.current

    // State to control showing the DatePickerDialog
    var showDatePicker by remember { mutableStateOf(false) }

    // If true, show the DatePickerDialog
    if (showDatePicker) {
        ShowDatePickerDialog(
            context = context,
            onDateSelected = { selectedDate ->
                // Format the date as needed (yyyy-MM-dd, for example)
                expirationDate = selectedDate
                showDatePicker = false
            },
            onDismissRequest = {
                // User canceled or dismissed the dialog
                showDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan or Add Item") },
                navigationIcon = {
                    // Provide a back arrow or similar
                }
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
            // Prompt user to choose an approach
            if (!showManualFields) {
                Text(
                    text = "Scan a New Item",
                    style = MaterialTheme.typography.headlineSmall
                )

                Button(
                    onClick = { showManualFields = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Manually")
                }

                Button(
                    onClick = {
                        // TODO: Launch camera scanning approach or navigate to a dedicated scanning screen
                        //navController.navigate(Screen.ScanBarcodeScreen.route)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Scan Barcode")
                }
            } else {
                // Show manual entry UI
                Text("Add Fridge Item Manually", style = MaterialTheme.typography.headlineSmall)

                TextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Instead of a TextField, use a Button or Box to display the chosen date and open the DatePicker
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // If the user hasn't picked a date, show placeholder text
                    // otherwise show the chosen date
                    val buttonText = if (expirationDate.isEmpty()) {
                        "Select Expiration Date"
                    } else {
                        "Expiration: $expirationDate"
                    }
                    Text(buttonText)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // 1. Validate user input
                        // 2. Possibly add to fridge (viewModel call or direct repository call)
                        // 3. Navigate back or show success
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Item")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // A "Back" button or bottom bar to return
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
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
    // Use a Calendar to get the current year, month, day
    val calendar = Calendar.getInstance()

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // month is 0-based, so add 1
            val pickedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            // Format the date as "yyyy-MM-dd"
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = format.format(pickedCalendar.time)

            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // If you'd like a minimum date (e.g. can't pick past dates), you can do:
    // datePicker.datePicker.minDate = calendar.timeInMillis

    // Show the dialog
    LaunchedEffect(Unit) {
        datePicker.show()
    }

    // Optionally, handle a dismiss callback if user cancels the dialog
    DisposableEffect(Unit) {
        onDispose {
            onDismissRequest()
        }
    }
}
