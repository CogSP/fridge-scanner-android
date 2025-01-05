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
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun BarcodeScannerScreen(navController: NavController, viewModel: FridgeViewModel) {

    val context = LocalContext.current

    var scannedCode by remember { mutableStateOf<String?>(null) }

    val options = GmsBarcodeScannerOptions.Builder()
        // if we want to only scan barcodes, we can specify it here, so that the search becomes faster
        //.setBarcodeFormats(
        //   Barcode.FORMAT_QR_CODE,
        //    Barcode.FORMAT_AZTEC)
        .enableAutoZoom() // available on 16.1.0 and higher
        .build()


    // Create the scanner client
    // NOTE: "this" should be an Activity or Context; in Composable, we use `context` from LocalContext
    val scanner = GmsBarcodeScanning.getClient(context, options)

    // Start scanning once on entering this composable
    // If you want to re-scan multiple times, adapt logic accordingly
    LaunchedEffect(Unit) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Task completed successfully
                scannedCode = barcode.rawValue // or handle other barcode data
            }
            .addOnCanceledListener {
                // Task was canceled (no barcode found)
                scannedCode = "Scan canceled"
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
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
            // Found a code OR canceled/failed
            Text(text = "Result: $scannedCode")
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
