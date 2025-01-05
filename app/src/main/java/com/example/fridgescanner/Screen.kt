package com.example.fridgescanner

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object HomePageScreen : Screen("home_page_screen")
    //object DetailScreen : Screen("detail_screen")
    object FridgeScreen : Screen("fridge_screen")
    object FridgeItemDetailScreen : Screen("fridgeItemDetail")
    object OptionsScreen : Screen("options_screen")
    object ScanScreen : Screen("scan_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}