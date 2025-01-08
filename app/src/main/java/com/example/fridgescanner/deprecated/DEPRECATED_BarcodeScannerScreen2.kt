package com.example.fridgescanner.deprecated

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.util.Log
import android.view.OrientationEventListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.fridgescanner.ui.BarcodeOverlay
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Composable
fun BarcodeScannerScreen(
    onBack: () -> Unit = {} // Callback when scanning is complete or user navigates back
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State holders for camera views
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var barcodeOverlay by remember { mutableStateOf<BarcodeOverlay?>(null) }

    // Camera executor and orientation listener
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var currentOrientation by remember { mutableStateOf(0) }

    // Permissions launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            previewView?.let { startCamera(context, it, barcodeOverlay, cameraExecutor, lifecycleOwner) }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Request camera permission if not granted
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            previewView?.let { startCamera(context, it, barcodeOverlay, cameraExecutor, lifecycleOwner) }
        }
    }

    // Orientation listener
    DisposableEffect(Unit) {
        val orientationListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                currentOrientation = when {
                    (orientation in 0 until 45) || (orientation >= 315) -> 0 // Portrait
                    (orientation in 135 until 225) -> 1 // Landscape
                    else -> currentOrientation
                }
            }
        }
        orientationListener.enable()
        onDispose {
            orientationListener.disable()
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // PreviewView for camera feed
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).also { pv ->
                    pv.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    previewView = pv
                }
            }
        )

        // BarcodeOverlay on top of PreviewView
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                BarcodeOverlay(ctx).also { overlay ->
                    overlay.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    barcodeOverlay = overlay
                }
            }
        )
    }
}

private fun startCamera(
    context: android.content.Context,
    previewView: PreviewView,
    barcodeOverlay: BarcodeOverlay?,
    cameraExecutor: ExecutorService,
    lifecycleOwner: LifecycleOwner
) {
    val TAG = "BarcodeScanner"
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(
                    AspectRatioStrategy(
                        AspectRatio.RATIO_16_9, // Preferred aspect ratio
                        AspectRatioStrategy.FALLBACK_RULE_AUTO // Automatic fallback
                    )
                )
                .build()

            // Camera selector for the back camera
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            // Preview use case
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }


            val imageAnalysis = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_ALL_FORMATS
                )
                .build()

            val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                processImageProxy(scanner, imageProxy, barcodeOverlay)
            }

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            Log.e(TAG, "Camera provider failed.", e)
        }
    }, ContextCompat.getMainExecutor(context))
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    barcodeOverlay: BarcodeOverlay?
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val mappedBoundingBoxes = mutableListOf<Rect>()
                    for (barcode in barcodes) {
                        barcode.boundingBox?.let { boundingBox ->
                            // A simple mapping (if not using custom mapping logic)
                            mappedBoundingBoxes.add(boundingBox)
                        }
                    }
                    barcodeOverlay?.updateBarcodes(mappedBoundingBoxes)
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeScanner", "Barcode detection failed.", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
