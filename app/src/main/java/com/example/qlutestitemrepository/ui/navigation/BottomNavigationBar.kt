package com.example.qlutestitemrepository.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.More,
        Screen.Settings
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    val icon = when (screen) {
                        Screen.Home -> Icons.Filled.Home
                        Screen.More -> Icons.Filled.Menu
                        Screen.Settings -> Icons.Filled.Settings
                        else -> Icons.Filled.Home
                    }
                    Icon(icon, contentDescription = null)
                },
                label = {
                     val label = when (screen) {
                        Screen.Home -> "Home"
                        Screen.More -> "More"
                        Screen.Settings -> "Settings"
                        else -> ""
                     }
                     Text(label)
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
