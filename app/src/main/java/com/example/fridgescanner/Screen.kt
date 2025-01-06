package com.example.fridgescanner

sealed class Screen(val route: String) {
    object LoginScreen : Screen("main_screen")
    object ForgotPasswordScreen : Screen("forgot_password_screen")
    object RegisterScreen : Screen("register_screen")
    object HomePageScreen : Screen("home_page_screen")
    object FridgeScreen : Screen("fridge_screen")
    object FridgeItemDetailScreen : Screen("fridgeItemDetail")
    object OptionsScreen : Screen("options_screen")
    object ScanScreen : Screen("scan_screen")
    object BarcodeScannerScreen : Screen("barcode_scanner_screen")
    object NotificationsScreen : Screen("notifications_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}