package com.example.fridgescanner.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException



@Composable
fun BarcodeScannerScreen(navController: NavController, viewModel: FridgeViewModel) {

    val context = LocalContext.current

    var scannedCode by remember { mutableStateOf<String?>(null) }
    var productInfo by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val options = GmsBarcodeScannerOptions.Builder()
        // if we want to only scan barcodes, we can specify it here, so that the search becomes faster
        //.setBarcodeFormats(
        //   Barcode.FORMAT_QR_CODE,
        //    Barcode.FORMAT_AZTEC)
        .enableAutoZoom() // available on 16.1.0 and higher
        .build()


    val scanner = GmsBarcodeScanning.getClient(context, options)

    // Start scanning once on entering this composable
    // If you want to re-scan multiple times, adapt logic accordingly
    // Start scanning once on entering this composable
    LaunchedEffect(Unit) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                scannedCode = barcode.rawValue
                if (!scannedCode.isNullOrBlank()) {
                    fetchProductInfo(
                        barcodeValue = scannedCode!!,
                        onSuccess = { info -> productInfo = info },
                        onFailure = { error -> errorMessage = error },
                        viewModel = viewModel
                    )
                }
            }
            .addOnCanceledListener {
                scannedCode = "Scan canceled"
            }
            .addOnFailureListener { e ->
                scannedCode = "Scan failed: ${e.localizedMessage}"
            }
    }


    // UI showing scanning status or the scanned result
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (scannedCode == null) {
            // In-progress
            Text(text = "Scanning in progress...")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Cancel")
            }
        } else {
            Text(text = "Scanned Code: $scannedCode")
            Spacer(modifier = Modifier.height(16.dp))

            when {
                productInfo != null -> {
                    Text(text = "Product Info: $productInfo")
                }
                errorMessage != null -> {
                    Text(text = "Error: $errorMessage", color = androidx.compose.ui.graphics.Color.Red)
                }
                else -> {
                    Text(text = "Fetching product information...")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Optionally store the code in the ViewModel
                viewModel.lastScannedCode = scannedCode as String
                // Return to previous screen or handle code
                navController.popBackStack()
            }) {
                Text("OK")
            }
        }
    }
}


fun fetchProductInfo(
    barcodeValue: String,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit,
    viewModel: FridgeViewModel
) {
    val client = OkHttpClient()
    val url = "https://world.openfoodfacts.org/api/v0/product/$barcodeValue.json"

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onFailure("Failed to fetch product info: ${e.localizedMessage}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use { res ->
                if (res.isSuccessful) {
                    val body = res.body?.string()
                    val json = JSONObject(body ?: "{}")
                    val product = json.optJSONObject("product")
                    if (product != null) {

                        val id = product.optString("code", "Unknown ID")
                        val productNameEN = product.optString("product_name_en", "Unknown Product")
                        val brand = product.optString("brands", "Unknown Brand")
                        val category = product.optString("categories", "Unknown Category")
                        val allergens = product.optString("allergens", "Unknown Allergens")
                        val conservationConditions = product.optString("conservation_conditions", "Unknown Conservation Conditions")
                        val countriesWhereSold = product.optString("countries", "Unknown Countries")
                        val countriesImported = product.optString("countries_imported", "Unknown Countries")
                        val ownerImported = product.optString("owner_imported", "Unknown Owner")
                        val preparation = product.optString("preparation", "Unknown Preparation")
                        val purchasePlaces = product.optString("purchase_places", "Unknown Purchase Places")
                        val productQuantity = product.optString("quantity", "Unknown Quantity")
                        val productQuantityUnit = product.optString("quantity_unit", "Unknown Quantity Unit")
                        val expirationDate = product.optString("expiration_date", "Unknown Expiration Date")

                        // check if this is always "food" and doesn't differentiate between edible and drinkable. In that case it's useless
                        val productType = product.optString("product_type", "Unknown Product Type")

                        val customerService = product.optString("customer_service", "Unknown Customer Service")
                        val imageUrl = product.optString("image_url", "Unknown Image URL")

                        val ingredientsImageEn = product
                            .optJSONObject("selected_images")
                            ?.optJSONObject("ingredients")
                            ?.optJSONObject("display")
                            ?.optString("en", "Unknown EN Ingredients Image URL")

                        // nutriments
                        val carbohydrates100g = product.optJSONObject("nutriments")?.optString("carbohydrates_100g", "Unknown Carbohydrates")
                        val energyKcal100g = product.optJSONObject("nutriments")?.optString("energy-kcal_100g", "Unknown Energy")
                        val fat100g = product.optJSONObject("nutriments")?.optString("fat_100g", "Unknown Fat")
                        val fiber100g = product.optJSONObject("nutriments")?.optString("fiber_100g", "Unknown Fiber")
                        val proteins100g = product.optJSONObject("nutriments")?.optString("proteins_100g", "Unknown Proteins")
                        val salt100g = product.optJSONObject("nutriments")?.optString("salt_100g", "Unknown Salt")
                        val saturatedFat100g = product.optJSONObject("nutriments")?.optString("saturated-fat_100g", "Unknown Saturated Fat")
                        val sodium100g = product.optJSONObject("nutriments")?.optString("sodium_100g", "Unknown Sodium")
                        val sugars100g = product.optJSONObject("nutriments")?.optString("sugars_100g", "Unknown Sugars")

                        val fridgeItem = FridgeItem(
                            id = id.toLong(),
                            name = productNameEN,
                            expirationDate = expirationDate,
                            quantity = 1,
                            brand = brand,
                            category = category,
                            allergens = allergens,
                            conservationConditions = conservationConditions,
                            countriesWhereSold = countriesWhereSold,
                            countriesImported = countriesImported,
                            ownerImported = ownerImported,
                            preparation = preparation,
                            purchasePlaces = purchasePlaces,
                            productQuantity = productQuantity,
                            productQuantityUnit = productQuantityUnit,
                            productType = productType,
                            customerService = customerService,
                            imageUrl = imageUrl,
                            ingredientsImageEn = ingredientsImageEn,
                            carbohydrates100g = carbohydrates100g,
                            energyKcal100g = energyKcal100g,
                            fat100g = fat100g,
                            fiber100g = fiber100g,
                            proteins100g = proteins100g,
                            salt100g = salt100g,
                            saturatedFat100g = saturatedFat100g,
                            sodium100g = sodium100g,
                            sugars100g = sugars100g
                        )

                        // add or update to fridge
                        viewModel.addOrUpdateFridgeItem(fridgeItem)


                        onSuccess("$productNameEN registered!")
                    } else {
                        onFailure("No product information found for barcode $barcodeValue")
                    }
                } else {
                    onFailure("API error: ${res.message}")
                }
            }
        }
    })
}
