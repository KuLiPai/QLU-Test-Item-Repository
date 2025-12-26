package com.example.qlutestitemrepository.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object More : Screen("more")
    object Settings : Screen("settings")
    object About : Screen("about")
}
