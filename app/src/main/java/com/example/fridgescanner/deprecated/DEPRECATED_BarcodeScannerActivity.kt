/*
package com.example.fridgescanner.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1001
        private const val TAG = "BarcodeScannerActivity"
    }

    private lateinit var cameraExecutor: ExecutorService
    private var currentOrientation = 0 // 0: portrait, 1: landscape

    // Lateinit variables to hold references from Compose
    private lateinit var barcodeOverlay: BarcodeOverlay
    private lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            BarcodeScannerScreen(
//                onPreviewViewReady = { preview ->
//                    previewView = preview
//                    // After previewView is ready and if permission is granted, start camera
//                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                        startCamera()
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this,
//                            arrayOf(Manifest.permission.CAMERA),
//                            REQUEST_CAMERA_PERMISSION
//                        )
//                    }
//                },
//                onOverlayReady = { overlay ->
//                    barcodeOverlay = overlay
//                }
            )
        }

        // Set up orientation listener
        val orientationEventListener = object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                currentOrientation = when {
                    (orientation in 0 until 45) || (orientation >= 315) -> 0 // Portrait
                    (orientation in 135 until 225) -> 1 // Landscape
                    else -> currentOrientation
                }
            }
        }
        orientationEventListener.enable()
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(cameraProvider)
            } catch (e: Exception) {
                Log.e(TAG, "Camera provider failed.", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)

        imageAnalysis.setAnalyzer(cameraExecutor) { image ->
            processImageProxy(scanner, image)
        }

        cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(scanner: BarcodeScanner, imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val mappedBoundingBoxes = mutableListOf<Rect>()
                        for (barcode in barcodes) {
                            barcode.boundingBox?.let { boundingBox ->
                                val rect = mapBoundingBoxToView(boundingBox, imageProxy)
                                mappedBoundingBoxes.add(rect)
                            }
                        }
                        barcodeOverlay.updateBarcodes(mappedBoundingBoxes)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Barcode detection failed.", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    fun mapBoundingBoxToView(boundingBox: Rect, imageProxy: ImageProxy): Rect {
        val imageWidth = imageProxy.width
        val imageHeight = imageProxy.height

        val viewWidth = previewView.width
        val viewHeight = previewView.height

        val scaleX = viewWidth.toFloat() / imageHeight.toFloat()
        val scaleY = viewHeight.toFloat() / imageWidth.toFloat()

        val offsetX = (viewWidth - imageHeight * scaleX) / 2
        val offsetY = (viewHeight - imageWidth * scaleY) / 2

        val left = (boundingBox.left * scaleX + offsetX).toInt()
        val top = (boundingBox.top * scaleY + offsetY).toInt()
        val right = (boundingBox.right * scaleX + offsetX).toInt()
        val bottom = (boundingBox.bottom * scaleY + offsetY).toInt()

        return Rect(left, top, right, bottom)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
*/