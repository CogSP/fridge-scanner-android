package com.example.fridgescanner.ui.scanitems

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.util.ToastHelper
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
//import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.fridgescanner.data.ProductRequest
import com.example.fridgescanner.data.ProductResponse
import com.example.fridgescanner.pythonanywhereAPI.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

@Composable
fun BarcodeScannerScreen(
    onBack: () -> Unit,
    viewModel: FridgeViewModel
) {
    val context = LocalContext.current

    var hasNavigatedState = remember { mutableStateOf(0) }

    // State to hold scanned barcodes
    //val scannedBarcodes = remember { mutableStateListOf<String>() }

    // TODO: check if this is better than the previous one
    var scannedBarcode by remember { mutableStateOf<String?>(null) }

    // Remember a camera executor for running tasks off the main thread
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Remember objects for PreviewView and OverlayView to be used in AndroidView
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var overlayView: OverlayView? by remember { mutableStateOf(null) }


    // Permission launcher for camera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                startCamera(
                    hasNavigatedState,
                    context = context,
                    previewView = previewView,
                    overlayView = overlayView,
                    //scannedBarcodes = scannedBarcodes,
                    cameraExecutor = cameraExecutor,
                    viewModel = viewModel,
                    onBack = onBack
                )
            } else {
                ToastHelper.showToast(context, "Camera permission denied")
            }
        }
    )

    // Check camera permission on composition
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // If permission granted, start camera
                startCamera(
                    hasNavigatedState,
                    context = context,
                    previewView = previewView,
                    overlayView = overlayView,
                    //scannedBarcodes = scannedBarcodes,
                    cameraExecutor = cameraExecutor,
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
            else -> {
                // Request permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // UI layout using Compose
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview using PreviewView
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { pv ->
                    previewView = pv
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay view for bounding box
        AndroidView(
            factory = { ctx ->
                // Pass null for AttributeSet as no XML attributes are required.
                OverlayView(ctx, null).also { ov ->
                    overlayView = ov
                }
            },
            modifier = Modifier.fillMaxSize()
        )

//        // Display scanned barcodes at the bottom
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .background(Color(0xAA000000))
//                .padding(8.dp)
//                .align(Alignment.BottomCenter)
//        ) {
//            items(scannedBarcodes) { barcode ->
//                Text(text = barcode, color = Color.White)
//            }
//        }

        // Use a simple Text composable for a single barcode:
        scannedBarcode?.let { barcode ->
            Text(
                text = barcode,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xAA000000))
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            ToastHelper.clearToast()
        }
    }
}

private fun startCamera(
    hasNavigatedState: MutableState<Int>,
    context: Context,
    previewView: PreviewView?,
    overlayView: OverlayView?,
    //scannedBarcodes: MutableList<String>,
    cameraExecutor: ExecutorService,
    viewModel: FridgeViewModel,
    onBack: () -> Unit
) {
    if (previewView == null || overlayView == null) return

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
        val imageAnalyzer = ImageAnalysis.Builder().build().also { analysis ->
            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(
                    hasNavigatedState,
                    barcodeScanner,
                    imageProxy,
                    previewView,
                    overlayView,
                    //scannedBarcodes,
                    viewModel,
                    onBack
                )
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                (context as? androidx.lifecycle.LifecycleOwner) ?: return@addListener,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (exc: Exception) {
            ToastHelper.showToast(context, "Camera initialization failed")
        }
    }, ContextCompat.getMainExecutor(context))
}


@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    hasNavigatedState: MutableState<Int>,
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    previewView: PreviewView,
    overlayView: OverlayView,
    //scannedBarcodes: MutableList<String>,
    viewModel: FridgeViewModel,
    onBack: () -> Unit  // New callback parameter
) {

    println("Entering processImageProxy with hasNavigatedState: $hasNavigatedState")


    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->

                Log.d("BarcodeScanner", "Barcodes PORCACCIO DIOOOOOOOOO: $barcodes")
                val firstBarcode = barcodes.firstOrNull()
                Log.d("BarcodeScanner", "First barcode: $firstBarcode")

                if (firstBarcode != null) {
                    Log.d("BarcodeScanner", "First barcode type: ${firstBarcode.valueType}")
                    // increase by 1
                    hasNavigatedState.value += 1
//                    val barcodeValue = when (firstBarcode.valueType) {
//                        Barcode.TYPE_TEXT -> firstBarcode.displayValue
//                        else -> null
//                    }
                    val barcodeValue = when (firstBarcode.valueType) {
                        Barcode.TYPE_TEXT, Barcode.TYPE_PRODUCT -> firstBarcode.displayValue
                        else -> null
                    }
                    Log.d("BarcodeScanner", "BARCODE VALUE DIO MAIALONEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE: $barcodeValue")
                    barcodeValue?.let { value ->

                        val context = previewView.context

                        Log.d("BarcodeScanner", "PROVAPROVAPROVA BARCODE VALUE TROIA LA MADONNNAAAAAQAQAAAA: $value")
                        if (hasNavigatedState.value == 1) {
                            Log.d("BarcodeScanner", "Barcode value TROIA LA MADONNNAAAAAQAQAAAA: $value")
                            barcodeValue?.let { value ->
                                if (hasNavigatedState.value == 1) {
                                    // Launch a coroutine on the main thread.
                                    CoroutineScope(Dispatchers.Main).launch {
                                        // Get the current fridge id from the ViewModel.
                                        // Ensure it's not null and convert to an Int; adjust if your data model differs.
                                        val currentFridgeIdStr = viewModel.currentFridgeId.value
                                        val fridgeId = currentFridgeIdStr?.toIntOrNull() ?: 0
                                        val productResponse = fetchProductInfoRetrofit(value, previewView.context, fridgeId, viewModel.selectedExpiryDate.value)
                                        if (productResponse != null && productResponse.success) {
                                            ToastHelper.showToast(previewView.context, "${productResponse.product_information?.name} registered!")
                                            onBack()  // navigate back or perform another action
                                        } else {
                                            ToastHelper.showToast(previewView.context, "No product info found for barcode $value")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    firstBarcode.boundingBox?.let {
                        updateOverlay(it, imageProxy, previewView, overlayView)
                    }
                }
            }

            .addOnFailureListener {
                ToastHelper.showToast(previewView.context, "Failed to scan barcode")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}


private fun updateOverlay(
    boundingBox: Rect,
    imageProxy: ImageProxy,
    previewView: PreviewView,
    overlayView: OverlayView
) {
    val overlayBoundingBox = transformBoundingBox(boundingBox, imageProxy, previewView)
    overlayView.qrCodeBounds = overlayBoundingBox
    overlayView.postInvalidate()
}

private fun transformBoundingBox(
    boundingBox: Rect,
    imageProxy: ImageProxy,
    previewView: PreviewView
): Rect {
    val previewWidth = previewView.width.toFloat()
    val previewHeight = previewView.height.toFloat()
    val imageWidth = imageProxy.width.toFloat()
    val imageHeight = imageProxy.height.toFloat()

    // Adjust for the aspect ratio
    val aspectRatioPreview = previewWidth / previewHeight
    val aspectRatioImage = imageHeight / imageWidth

    val scaleFactor: Float
    val dx: Float
    val dy: Float

    if (aspectRatioPreview > aspectRatioImage) {
        scaleFactor = previewWidth / imageHeight
        dx = 0f
        dy = (previewHeight - imageWidth * scaleFactor) / 2f
    } else {
        scaleFactor = previewHeight / imageWidth
        dx = (previewWidth - imageHeight * scaleFactor) / 2f
        dy = 0f
    }

    val matrix = Matrix().apply {
        postScale(scaleFactor, scaleFactor)
        postTranslate(dx, dy)
    }

    val rectF = RectF(boundingBox)
    matrix.mapRect(rectF)

    return Rect(
        rectF.left.toInt(),
        rectF.top.toInt(),
        rectF.right.toInt(),
        rectF.bottom.toInt()
    )
}




suspend fun fetchProductInfoRetrofit(
    barcodeValue: String,
    context: Context,
    fridgeId: Int,
    expiryDate: String
): ProductResponse? {
    return try {
        // Call your PythonAnywhere endpoint via Retrofit.
        Log.d("Retrofit", "Fetching product info for barcode: $barcodeValue, fridgeId: $fridgeId, expiryDate: $expiryDate")
        val response: Response<ProductResponse> = ApiClient.fridgeApiService.getProduct(
            ProductRequest(id = barcodeValue, fridge_id = fridgeId, expiry_date = expiryDate)
        )
        Log.d("Retrofit", "Response: $response")
        if (response.isSuccessful) {
            response.body()
        } else {
            ToastHelper.showToast(context, "Error: ${response.code()}")
            null
        }
    } catch (e: Exception) {
        ToastHelper.showToast(context, "Exception: ${e.localizedMessage}")
        null
    }
}


//fun fetchProductInfo(
//    barcodeValue: String,
//    onSuccess: (String) -> Unit,
//    onFailure: (String) -> Unit,
//    viewModel: FridgeViewModel,
//    context: Context // Pass context to use in ToastHelper
//) {
//
//    println("Fetching product info for barcode: $barcodeValue")
//
//    val client = OkHttpClient()
//    val url = "https://world.openfoodfacts.org/api/v0/product/$barcodeValue.json"
//
//    val request = Request.Builder()
//        .url(url)
//        .build()
//
//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            // Use ToastHelper to show failure message
//            ToastHelper.showToast(context, "Failed to fetch product info: ${e.localizedMessage}")
//            onFailure("Failed to fetch product info: ${e.localizedMessage}")
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//
//            println("OnResponse called with response: $response")
//
//            response.use { res ->
//                if (res.isSuccessful) {
//                    val body = res.body?.string()
//                    val json = JSONObject(body ?: "{}")
//                    val product = json.optJSONObject("product")
//                    if (product != null) {
//                        // Extract product details...
//                        val id = product.optString("code", "0").toLongOrNull() ?: 0L
//                        var productNameEN =
//                            product.optString("product_name_en", "_")
//
//                        if (productNameEN == "" || productNameEN == "_") {
//                            productNameEN = product.optString("product_name", "_")
//                        }
//
//
//                        val brand = product.optString("brands", "_")
//                        val category = product.optString("categories", "_")
//                        // Read the raw allergens string
//                        val rawAllergens = product.optString("allergens", "_")
//
//                        // Split by commas, remove "en:", and join back together as needed
//                        val allergens = if (rawAllergens != "_") {
//                            rawAllergens
//                                .split(",")
//                                .map { it.removePrefix("en:").trim() } // Remove "en:" and trim spaces
//                                .joinToString(", ")
//                        } else {
//                            "_"
//                        }
//
//                        val conservationConditions = product.optString(
//                            "conservation_conditions",
//                            "_"
//                        )
//
//                        val countriesHierarchy = product.optJSONArray("countries_hierarchy")
//                        val countriesWhereSold: String
//
//                        if (countriesHierarchy != null && countriesHierarchy.length() > 0) {
//                            // Build a list of country strings from the array
//                            val countryList = mutableListOf<String>()
//                            for (i in 0 until countriesHierarchy.length()) {
//                                // read the string, then remove the "en:" prefix if present
//                                val rawCountry = countriesHierarchy.optString(i)
//                                val countryWithoutPrefix = rawCountry.removePrefix("en:")
//                                if (countryWithoutPrefix.isNotEmpty()) {
//                                    countryList.add(countryWithoutPrefix)
//                                }
//                            }
//                            // Join them into a single comma-separated string (or any format you prefer)
//                            countriesWhereSold = countryList.joinToString(separator = ", ")
//                        } else {
//                            countriesWhereSold = "_"
//                        }
//
//                        val countriesImported =
//                            product.optString("countries_imported", "_")
//                        val ownerImported = product.optString("owner_imported", "_")
//                        val preparation =
//                            product.optString("preparation", "_")
//                        val purchasePlaces =
//                            product.optString("purchase_places", "_")
//                        val productQuantity = product.optString("quantity", "_")
//                        val productQuantityUnit =
//                            product.optString("quantity_unit", "_")
//                        val expiry_date =
//                            product.optString("expiration_date", "_")
//                        val productType =
//                            product.optString("product_type", "_")
//                        val customerService =
//                            product.optString("customer_service", "_")
//                        val imageUrl = product.optString("image_url", "_")
//                        val ingredientsImageEn = product
//                            .optJSONObject("selected_images")
//                            ?.optJSONObject("ingredients")
//                            ?.optJSONObject("display")
//                            ?.optString("en", "_")
//
//                        // Nutriments...
//                        val carbohydrates100g =
//                            product.optJSONObject("nutriments")?.optString("carbohydrates_100g")
//                        val energyKcal100g =
//                            product.optJSONObject("nutriments")?.optString("energy-kcal_100g")
//                        val fat100g = product.optJSONObject("nutriments")?.optString("fat_100g")
//                        val fiber100g =
//                            product.optJSONObject("nutriments")?.optString("fiber_100g")
//                        val proteins100g =
//                            product.optJSONObject("nutriments")?.optString("proteins_100g")
//                        val salt100g =
//                            product.optJSONObject("nutriments")?.optString("salt_100g")
//                        val saturatedFat100g =
//                            product.optJSONObject("nutriments")?.optString("saturated-fat_100g")
//                        val sodium100g =
//                            product.optJSONObject("nutriments")?.optString("sodium_100g")
//                        val sugars100g =
//                            product.optJSONObject("nutriments")?.optString("sugars_100g")
//
//                        val fridgeItem = FridgeItem(
//                            id = id,
//                            name = productNameEN,
//                            expiry_date = expiry_date,
//                            quantity = 1,
//                            brand = brand,
//                            category = category,
//                            allergens = allergens,
//                            conservationConditions = conservationConditions,
//                            countriesWhereSold = countriesWhereSold,
//                            countriesImported = countriesImported,
//                            ownerImported = ownerImported,
//                            preparation = preparation,
//                            purchasePlaces = purchasePlaces,
//                            productQuantity = productQuantity,
//                            productQuantityUnit = productQuantityUnit,
//                            productType = productType,
//                            customerService = customerService,
//                            imageUrl = imageUrl,
//                            ingredientsImageEn = ingredientsImageEn,
//                            carbohydrates100g = carbohydrates100g,
//                            energyKcal100g = energyKcal100g,
//                            fat100g = fat100g,
//                            fiber100g = fiber100g,
//                            proteins100g = proteins100g,
//                            salt100g = salt100g,
//                            saturatedFat100g = saturatedFat100g,
//                            sodium100g = sodium100g,
//                            sugars100g = sugars100g
//                        )
//
//                        // Add or update to fridge using ViewModel
//                        viewModel.addOrUpdateFridgeItem(fridgeItem)
//
//                        // Use ToastHelper to show success message
//                        ToastHelper.showToast(context, "${fridgeItem.name} registered!")
//                        onSuccess("${fridgeItem.name} registered!")
//                    } else {
//                        // Switch to the main thread to show the Toast
//                        ToastHelper.showToast(
//                            context,
//                            "No product information found for barcode $barcodeValue"
//                        )
//                        onFailure("No product information found for barcode $barcodeValue")
//                    }
//                } else {
//                    // Use ToastHelper to show API error
//                    ToastHelper.showToast(context, "API error: ${res.message}")
//                    onFailure("API error: ${res.message}")
//                }
//            }
//        }
//    })
//}
//
