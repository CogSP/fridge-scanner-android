package com.example.fridgescanner.ui

import ScanScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fridgescanner.Screen
import com.example.fridgescanner.data.FridgeItem
import com.example.fridgescanner.data.FridgeRepository
import com.example.fridgescanner.ui.FridgeItemCard
import com.example.fridgescanner.ui.FridgeScreen
import com.example.fridgescanner.viewmodel.FridgeViewModel
import com.example.fridgescanner.viewmodel.FridgeViewModelFactory

@Composable
fun Navigation() {

    val navController = rememberNavController()

    val repository = remember { FridgeRepository() }
    val viewModelFactory = remember { FridgeViewModelFactory(repository) }
    val fridgeViewModel: FridgeViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController, startDestination = Screen.MainScreen.route) {
        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.DetailScreen.route + "/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                    defaultValue = "Simone"
                    nullable = true
                }
            )
        ) { entry ->
            DetailScreen(name = entry.arguments?.getString("name"), navController = navController, viewModel = fridgeViewModel)
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
    }
}

