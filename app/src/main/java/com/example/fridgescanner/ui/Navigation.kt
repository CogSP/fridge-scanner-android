package com.example.fridgescanner.ui


import ResetPasswordScreen
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.fridgescanner.promo.ProPromoScreen
import com.example.fridgescanner.ui.account.AccountScreen
import com.example.fridgescanner.ui.scanitems.BarcodeScannerScreen
import com.example.fridgescanner.ui.fridgeui.FridgeItemDetailScreen
import com.example.fridgescanner.ui.fridgeui.FridgeScreen
import com.example.fridgescanner.ui.fridgeui.ManageFridgesScreen
import com.example.fridgescanner.ui.login.ForgotPasswordScreen
import com.example.fridgescanner.ui.login.LoginScreen
import com.example.fridgescanner.ui.login.RegisterScreen
import com.example.fridgescanner.ui.notification.NotificationsScreen
import com.example.fridgescanner.ui.options.OptionsScreen
import com.example.fridgescanner.ui.scanitems.ScanScreen
import com.example.fridgescanner.ui.shoppinglist.ShoppingListScreen
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.example.fridgescanner.viewmodel.FridgeViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(fridgeViewModel: FridgeViewModel) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.LoginScreen.route) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(navController = navController, viewModel = fridgeViewModel)
        }
        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController, fridgeViewModel)
        }
//        composable(Screen.ResetPasswordScreen.route) {
//            ResetPasswordScreen(navController, fridgeViewModel)
//        }
        composable(
            route = Screen.ResetPasswordScreen.route + "/{email}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { entry ->
            ResetPasswordScreen(navController = navController, viewModel = fridgeViewModel, email = entry.arguments?.getString("email") ?: "")
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
        composable(Screen.ManageFridgesScreen.route) {
            ManageFridgesScreen(navController, fridgeViewModel)
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
                initialFilter = filter
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

        composable("barcodeScanner") {
            BarcodeScannerScreen(
                onBack = { navController.popBackStack() },
                viewModel = fridgeViewModel
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

        composable(Screen.ProPromoScreen.route) {
            ProPromoScreen(navController = navController)
        }

    }
}


