package com.example.fridgescanner.deprecated

import CameraPreviewWithBarcodeScanner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.google.accompanist.permissions.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.google.mlkit.vision.barcode.common.Barcode
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreenWithOverlay(navController: NavController, viewModel: FridgeViewModel) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var isPreviewReady by remember { mutableStateOf(false) }
    var showScannedOverlay by remember { mutableStateOf(false) }

    // State to hold the list of detected barcodes
    var detectedBarcodes by remember { mutableStateOf<List<Barcode>>(emptyList()) }
    var rotationDegrees by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }

    if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreviewWithBarcodeScanner(
                modifier = Modifier.fillMaxSize(),
                onPreviewReady = { isPreviewReady = true },
                onBarcodesDetected = { barcodes ->
                    detectedBarcodes = barcodes
                    if (barcodes.isNotEmpty()) {
                        showScannedOverlay = true
                        // Optionally fetch product info for first barcode:
                        fetchProductInfo(
                            barcodeValue = barcodes.first().rawValue ?: "",
                            onSuccess = { /* handle success */ },
                            onFailure = { /* handle error */ },
                            viewModel = viewModel
                        )
                    }
                } ,
                onRotationExtracted = { rotation ->
                    rotationDegrees = rotation
                }
            )

            if (!isPreviewReady) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }


            // Draw rectangles for each detected barcode
            detectedBarcodes.forEach { barcode ->
                barcode.boundingBox?.let { rect ->

//                    TransformedBoundingBoxCanvas(
//                        imageWidth = 1280f,
//                        imageHeight = 720f,
//                        boundingBox = rect,
//                        rotationAngleDegrees = rotationDegrees
//                    )
                    BoundingBoxCanvas(
                        boundingBox = rect
                    )
                }
            }



            // Overlay "Barcode Scanned!" text as before
            if (showScannedOverlay) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showScannedOverlay = false
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Barcode Scanned!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                            .padding(16.dp)
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera permission is required.")
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
                        // Extract product details...
                        val id = product.optString("code", "0").toLongOrNull() ?: 0L
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
                        val productType = product.optString("product_type", "Unknown Product Type")
                        val customerService = product.optString("customer_service", "Unknown Customer Service")
                        val imageUrl = product.optString("image_url", "Unknown Image URL")
                        val ingredientsImageEn = product
                            .optJSONObject("selected_images")
                            ?.optJSONObject("ingredients")
                            ?.optJSONObject("display")
                            ?.optString("en", "Unknown EN Ingredients Image URL")

                        // Nutriments...
                        val carbohydrates100g = product.optJSONObject("nutriments")?.optString("carbohydrates_100g")
                        val energyKcal100g = product.optJSONObject("nutriments")?.optString("energy-kcal_100g")
                        val fat100g = product.optJSONObject("nutriments")?.optString("fat_100g")
                        val fiber100g = product.optJSONObject("nutriments")?.optString("fiber_100g")
                        val proteins100g = product.optJSONObject("nutriments")?.optString("proteins_100g")
                        val salt100g = product.optJSONObject("nutriments")?.optString("salt_100g")
                        val saturatedFat100g = product.optJSONObject("nutriments")?.optString("saturated-fat_100g")
                        val sodium100g = product.optJSONObject("nutriments")?.optString("sodium_100g")
                        val sugars100g = product.optJSONObject("nutriments")?.optString("sugars_100g")

                        val fridgeItem = FridgeItem(
                            id = id,
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

                        // Add or update to fridge using ViewModel
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



@Composable
fun TransformedBoundingBoxCanvas(
    imageWidth: Float,
    imageHeight: Float,
    boundingBox: android.graphics.Rect,          // Bounding box in image space
    rotationAngleDegrees: Float // Rotation angle in degrees from image to canvas
) {
    println("TransformedBoundingBoxCanvas called")
    println("imageWidth: $imageWidth")
    println("imageHeight: $imageHeight")
    println("boundingBox: $boundingBox")
    println("rotationAngleDegrees: $rotationAngleDegrees")


    Canvas(modifier = Modifier.fillMaxSize()) {
        // 1. Calculate scale factors for X and Y axes
        val scaleX = size.width / imageWidth
        val scaleY = size.height / imageHeight

        // 2. Convert rotation angle to radians
        val theta = Math.toRadians(rotationAngleDegrees.toDouble())
        val cosTheta = cos(theta).toFloat()
        val sinTheta = sin(theta).toFloat()

        // 3. Define translation offsets if necessary (set to zero here)
        val tX = 0f
        val tY = 0f

        // 4. Build the transformation matrix components
        val a = scaleX * cosTheta
        val b = -scaleY * sinTheta
        val c = scaleX * sinTheta
        val d = scaleY * cosTheta

        // 5. Function to transform a point from image space to canvas space
        fun transformPoint(imgX: Float, imgY: Float): Offset {
            val canvasX = a * imgX + b * imgY + tX
            val canvasY = c * imgX + d * imgY + tY
            return Offset(canvasX, canvasY)
        }

        // 6. Extract the corners of the bounding box in image space
        val topLeft = Offset(boundingBox.left.toFloat(), boundingBox.top.toFloat())
        val topRight = Offset(boundingBox.right.toFloat(), boundingBox.top.toFloat())
        val bottomRight = Offset(boundingBox.right.toFloat(), boundingBox.bottom.toFloat())
        val bottomLeft = Offset(boundingBox.left.toFloat(), boundingBox.bottom.toFloat())

        // 7. Transform each corner to canvas space
        val canvasTopLeft = transformPoint(topLeft.x, topLeft.y)
        val canvasTopRight = transformPoint(topRight.x, topRight.y)
        val canvasBottomRight = transformPoint(bottomRight.x, bottomRight.y)
        val canvasBottomLeft = transformPoint(bottomLeft.x, bottomLeft.y)

        // 8. Draw the transformed bounding box using a Path
        val path = Path().apply {
            moveTo(canvasTopLeft.x, canvasTopLeft.y)
            lineTo(canvasTopRight.x, canvasTopRight.y)
            lineTo(canvasBottomRight.x, canvasBottomRight.y)
            lineTo(canvasBottomLeft.x, canvasBottomLeft.y)
            close()
        }

        drawPath(
            path = path,
            color = Color.Red,
            style = Stroke(width = 4f)
        )
    }
}



@Composable
fun BoundingBoxCanvas(
    boundingBox: android.graphics.Rect,
) {

    Canvas(modifier = Modifier.fillMaxSize()) { // Use the modifier
        drawRect(
            color = Color.Red,
            topLeft = Offset(
                boundingBox.left.toFloat(),
                boundingBox.top.toFloat()
            ),
            size = Size(
                boundingBox.width().toFloat(),
                boundingBox.height().toFloat()
            ),
            style = Stroke(width = 4.dp.toPx()) // Convert DP to PX
        )
    }

}
