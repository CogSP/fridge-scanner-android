import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.fridgescanner.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(navController: NavController, viewModel: FridgeViewModel) {
    // Determines whether to show manual fields or just the "Scan" options
    var showManualFields by remember { mutableStateOf(false) }

    // Item states for manual entry
    var itemName by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var allergens by remember { mutableStateOf("") }
    var quantityText by remember { mutableStateOf("1") } // We'll parse this as Int

    // Advanced fields (expandable)
    var preparation by remember { mutableStateOf("") }
    var purchasePlaces by remember { mutableStateOf("") }
    var conservationConditions by remember { mutableStateOf("") }
    var countriesWhereSold by remember { mutableStateOf("") }
    var countriesImported by remember { mutableStateOf("") }
    var ownerImported by remember { mutableStateOf("") }
    var productQuantity by remember { mutableStateOf("") }
    var productQuantityUnit by remember { mutableStateOf("") }
    var productType by remember { mutableStateOf("") }
    var customerService by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var ingredientsImageEn by remember { mutableStateOf("") }
    var carbohydrates100g by remember { mutableStateOf("") }
    var energyKcal100g by remember { mutableStateOf("") }
    var fat100g by remember { mutableStateOf("") }
    var fiber100g by remember { mutableStateOf("") }
    var proteins100g by remember { mutableStateOf("") }
    var salt100g by remember { mutableStateOf("") }
    var saturatedFat100g by remember { mutableStateOf("") }
    var sodium100g by remember { mutableStateOf("") }
    var sugars100g by remember { mutableStateOf("") }


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
                            .fillMaxSize() // Fill the card
                            .verticalScroll(rememberScrollState()), // Scroll if content is large
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Item Name
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text("Item Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Brand
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Category
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = { Text("Category") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Allergens
                        OutlinedTextField(
                            value = allergens,
                            onValueChange = { allergens = it },
                            label = { Text("Allergens") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Quantity
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { quantityText = it },
                            label = { Text("Quantity") },
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

                        // Expandable for advanced fields
                        ExpandableSection(title = "Advanced Info") {
                            OutlinedTextField(
                                value = preparation,
                                onValueChange = { preparation = it },
                                label = { Text("Preparation") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = purchasePlaces,
                                onValueChange = { purchasePlaces = it },
                                label = { Text("Purchase Places") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = conservationConditions,
                                onValueChange = { conservationConditions = it },
                                label = { Text("Conservation Conditions") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = countriesWhereSold,
                                onValueChange = { countriesWhereSold = it },
                                label = { Text("Countries Where Sold") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = countriesImported,
                                onValueChange = { countriesImported = it },
                                label = { Text("Countries Imported") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = ownerImported,
                                onValueChange = { ownerImported = it },
                                label = { Text("Owner Imported") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = productQuantity,
                                onValueChange = { productQuantity = it },
                                label = { Text("Product Quantity") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = productQuantityUnit,
                                onValueChange = { productQuantityUnit = it },
                                label = { Text("Product Quantity Unit") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = productType,
                                onValueChange = { productType = it },
                                label = { Text("Product Type") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = customerService,
                                onValueChange = { customerService = it },
                                label = { Text("Customer Service") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = imageUrl,
                                onValueChange = { imageUrl = it },
                                label = { Text("Image URL") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = ingredientsImageEn,
                                onValueChange = { ingredientsImageEn = it },
                                label = { Text("Ingredients Image URL") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = carbohydrates100g,
                                onValueChange = { carbohydrates100g = it },
                                label = { Text("Carbohydrates 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = energyKcal100g,
                                onValueChange = { energyKcal100g = it },
                                label = { Text("Energy Kcal 100g") },
                                modifier = Modifier.fillMaxWidth()

                            )

                            OutlinedTextField(
                                value = fat100g,
                                onValueChange = { fat100g = it },
                                label = { Text("Fat 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = fiber100g,
                                onValueChange = { fiber100g = it },
                                label = { Text("Fiber 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = proteins100g,
                                onValueChange = { proteins100g = it },
                                label = { Text("Proteins 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = salt100g,
                                onValueChange = { salt100g = it },
                                label = { Text("Salt 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = saturatedFat100g,
                                onValueChange = { saturatedFat100g = it },
                                label = { Text("Saturated Fat 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = sodium100g,
                                onValueChange = { sodium100g = it },
                                label = { Text("Sodium 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = sugars100g,
                                onValueChange = { sugars100g = it },
                                label = { Text("Sugars 100g") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Button(
                            onClick = {
                                // Validate inputs
                                val parsedQuantity = quantityText.toIntOrNull() ?: 1
                                val finalExpiration = if (expirationDate.isBlank()) "2025-01-01" else expirationDate
                                val finalName = if (itemName.isBlank()) "Unknown" else itemName

                                val newItem = com.example.fridgescanner.data.FridgeItem(
                                    id = System.currentTimeMillis(), // unique ID
                                    name = finalName,
                                    expirationDate = finalExpiration,
                                    quantity = parsedQuantity,
                                    brand = brand.ifBlank { "Unknown Brand" },
                                    category = category.ifBlank { "Unknown Category" },
                                    allergens = allergens.ifBlank { "None" },
                                    conservationConditions = conservationConditions.ifBlank { "Unknown" },
                                    countriesWhereSold = countriesWhereSold.ifBlank { "Unknown" },
                                    countriesImported = countriesImported.ifBlank { "Unknown" },
                                    ownerImported = ownerImported.ifBlank { "Unknown" },
                                    preparation = preparation.ifBlank { "N/A" },
                                    purchasePlaces = purchasePlaces.ifBlank { "N/A" },
                                    productQuantity = productQuantity.ifBlank { "0" },
                                    productQuantityUnit = productQuantityUnit.ifBlank { "N/A" },
                                    productType = productType.ifBlank { "food" },
                                    customerService = customerService.ifBlank { "N/A" },
                                    imageUrl = imageUrl.ifBlank { "N/A" },
                                    ingredientsImageEn = ingredientsImageEn.ifBlank { "N/A" },
                                    carbohydrates100g = carbohydrates100g.ifBlank { "0" },
                                    energyKcal100g = energyKcal100g.ifBlank { "0" },
                                    fat100g = fat100g.ifBlank { "0" },
                                    fiber100g = fiber100g.ifBlank { "0" },
                                    proteins100g = proteins100g.ifBlank { "0" },
                                    salt100g = salt100g.ifBlank { "0" },
                                    saturatedFat100g = saturatedFat100g.ifBlank { "0" },
                                    sodium100g = sodium100g.ifBlank { "0" },
                                    sugars100g = sugars100g.ifBlank { "0" }
                                )

                                // Add or update in repository
                                viewModel.addOrUpdateFridgeItem(newItem)

                                // Possibly navigate back or show success message
                                navController.popBackStack()
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

@Composable
fun ExpandableSection(
    title: String,
    initiallyExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Expand/Collapse Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }

        // Content (only if expanded)
        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),   // Add some internal padding
                verticalArrangement = Arrangement.spacedBy(8.dp) // spacing between fields
            ) {
                content()
            }
        }
    }
}
