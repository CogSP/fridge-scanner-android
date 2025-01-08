package com.example.fridgescanner.ui

import ScanScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fridgescanner.Screen
import com.example.fridgescanner.data.FridgeRepository
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.example.fridgescanner.viewmodel.FridgeViewModelFactory

@Composable
fun Navigation() {

    val navController = rememberNavController()

    val repository = remember { FridgeRepository() }
    val viewModelFactory = remember { FridgeViewModelFactory(repository) }
    val fridgeViewModel: FridgeViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController, viewModel = fridgeViewModel)
        }
        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController, fridgeViewModel)
        }
        composable(Screen.RegisterScreen.route) {
            RegisterScreen(navController, fridgeViewModel)
        }
        composable(
            route = Screen.HomePageScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Simone"
                    nullable = true
                }
            )
        ) { entry ->
            HomePageScreen(name = entry.arguments?.getString("name"), navController = navController, viewModel = fridgeViewModel)
        }
        composable(
            route = Screen.FridgeScreen.route + "/{filter}",
            arguments = listOf(
                navArgument("filter") {
                    type = NavType.StringType
                    defaultValue = "All" // Default filter if none provided
                    nullable = true
                }
            )
        ) { entry ->
            val filter = entry.arguments?.getString("filter") ?: "All"
            FridgeScreen(
                navController = navController,
                viewModel = fridgeViewModel,
                initialFilter = filter // Pass the filter to FridgeScreen
            )
        }

        // Preserve the original route without parameters if needed
        // Actually I don't think this is needed
        composable(
            route = Screen.FridgeScreen.route
        ) {
            FridgeScreen(
                navController = navController,
                viewModel = fridgeViewModel
                // default initialFilter will be used here
            )
        }

        composable(
            route = Screen.FridgeItemDetailScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val itemId = entry.arguments?.getLong("id") ?: 0
            FridgeItemDetailScreen(
                itemId = itemId,
                navController = navController,
                viewModel = fridgeViewModel
            )
        }

        composable(route = Screen.OptionsScreen.route) {
            OptionsScreen(navController, fridgeViewModel)
        }

        composable(Screen.ScanScreen.route) {
            ScanScreen(navController = navController, fridgeViewModel)
        }

//        composable(Screen.CameraScreenWithOverlay.route) {
//            //BarcodeScannerScreen(navController = navController, fridgeViewModel)
//            CameraScreenWithOverlay(navController = navController, fridgeViewModel)
//        }

        composable("barcodeScanner") {
            BarcodeScannerScreen(
                onBack = { navController.popBackStack() }
            )
        }


        composable(Screen.NotificationsScreen.route) {
            NotificationsScreen(navController)
        }

        composable(Screen.ShoppingListScreen.route) {
            ShoppingListScreen(navController = navController, fridgeViewModel)
        }

        composable(Screen.AccountScreen.route) {
            AccountScreen(navController = navController, viewModel = fridgeViewModel)
        }

    }
}


