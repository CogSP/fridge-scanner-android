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
            route = Screen.FridgeScreen.route
        ) {
            FridgeScreen(
                navController = navController,
                viewModel = fridgeViewModel)
        }
        composable(
            route = Screen.FridgeItemDetailScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val itemId = entry.arguments?.getInt("id") ?: 0
            // Provide the ViewModel using the factory
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

        composable(Screen.BarcodeScannerScreen.route) {
            BarcodeScannerScreen(navController = navController, fridgeViewModel)
        }
    }
}


