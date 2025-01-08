package com.example.fridgescanner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun BarcodeScannerScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // State to hold scanned barcodes
    val scannedBarcodes = remember { mutableStateListOf<String>() }

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
                    context = context,
                    previewView = previewView,
                    overlayView = overlayView,
                    scannedBarcodes = scannedBarcodes,
                    cameraExecutor = cameraExecutor
                )
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
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
                    context = context,
                    previewView = previewView,
                    overlayView = overlayView,
                    scannedBarcodes = scannedBarcodes,
                    cameraExecutor = cameraExecutor
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

        // Display scanned barcodes at the bottom
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color(0xAA000000))
                .padding(8.dp)
                .align(Alignment.BottomCenter)
        ) {
            items(scannedBarcodes) { barcode ->
                Text(text = barcode, color = Color.White)
            }
        }
    }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

private fun startCamera(
    context: android.content.Context,
    previewView: PreviewView?,
    overlayView: OverlayView?,
    scannedBarcodes: MutableList<String>,
    cameraExecutor: ExecutorService
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
                    barcodeScanner,
                    imageProxy,
                    previewView,
                    overlayView,
                    scannedBarcodes
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
            Toast.makeText(context, "Camera initialization failed", Toast.LENGTH_SHORT).show()
        }
    }, ContextCompat.getMainExecutor(context))
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    previewView: PreviewView,
    overlayView: OverlayView,
    scannedBarcodes: MutableList<String>
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                val newScanned = mutableListOf<String>()
                for (barcode in barcodes) {
                    when (barcode.valueType) {
                        Barcode.TYPE_URL -> {
                            barcode.url?.url?.let { newScanned.add(it) }
                            barcode.boundingBox?.let {
                                updateOverlay(it, imageProxy, previewView, overlayView)
                            }
                        }
                        Barcode.TYPE_TEXT -> {
                            barcode.displayValue?.let { newScanned.add(it) }
                            barcode.boundingBox?.let {
                                updateOverlay(it, imageProxy, previewView, overlayView)
                            }
                        }
                    }
                }
                scannedBarcodes.clear()
                scannedBarcodes.addAll(newScanned)
            }
            .addOnFailureListener {
                Toast.makeText(previewView.context, "Failed to scan barcode", Toast.LENGTH_SHORT).show()
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
