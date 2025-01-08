import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Size
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.compose.ui.graphics.toArgb


@Composable
fun CameraPreviewWithBarcodeScanner(
    modifier: Modifier = Modifier,
    onBarcodesDetected: (List<Barcode>) -> Unit = {},
    onPreviewReady: (PreviewView) -> Unit = {},
    onRotationExtracted: (Float) -> Unit = {}
) {
    val context = LocalContext.current
    val cameraExecutor = Executors.newSingleThreadExecutor()
    val barcodeScanner = BarcodeScanning.getClient()

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            // Create and add the custom overlay
            val overlayView = BarcodeOverlayView(ctx)
            previewView.overlay.add(overlayView)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(
                        barcodeScanner,
                        imageProxy,
                        onBarcodesDetected = { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                onBarcodesDetected(barcodes)
                                // Update overlay view with new barcodes if needed
                            }
                        },
                        onRotationExtracted = { rotation ->
                            onRotationExtracted(rotation)
                        }
                    )
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        ctx as? androidx.lifecycle.LifecycleOwner ?: return@addListener,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch(exc: Exception) {
                    // Handle exceptions as needed
                }
                onPreviewReady(previewView)
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )
}


@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onBarcodesDetected: (List<Barcode>) -> Unit,
    onRotationExtracted: (Float) -> Unit
) {
    val mediaImage = imageProxy.image
    // Extract rotation degrees early
    val rotation = imageProxy.imageInfo.rotationDegrees.toFloat()
    onRotationExtracted(rotation)
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                onBarcodesDetected(barcodes)
            }
            .addOnFailureListener {
                // Handle any errors during barcode scanning
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}


class BarcodeOverlayView(context: Context) : View(context) {
    var barcodes: List<Barcode> = emptyList()
    private val paint = Paint().apply {
        color = Color.Red.toArgb() // convert color to Int
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.let { // Use safe call and let
            barcodes.forEach { barcode ->
                barcode.boundingBox?.let { rect ->
                    // Use four float parameters for drawRect
                    it.drawRect(
                        rect.left.toFloat(),
                        rect.top.toFloat(),
                        rect.right.toFloat(),
                        rect.bottom.toFloat(),
                        paint
                    )
                }
            }
        }
    }
}