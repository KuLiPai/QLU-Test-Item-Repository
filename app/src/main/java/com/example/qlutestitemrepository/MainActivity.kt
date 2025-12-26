package com.example.qlutestitemrepository

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.qlutestitemrepository.ui.HomeViewModel
import com.example.qlutestitemrepository.ui.MainViewModel
import com.example.qlutestitemrepository.ui.navigation.BottomNavigationBar
import com.example.qlutestitemrepository.ui.navigation.Screen
import com.example.qlutestitemrepository.ui.screens.AboutScreen
import com.example.qlutestitemrepository.ui.screens.HomeScreen
import com.example.qlutestitemrepository.ui.screens.MoreScreen
import com.example.qlutestitemrepository.ui.screens.SettingsScreen
import com.example.qlutestitemrepository.ui.theme.QLUTestItemRepositoryTheme

class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QLUTestItemRepositoryTheme(
                darkTheme = mainViewModel.isDarkTheme,
                themeColor = mainViewModel.themeColor
            ) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.More.route,
                    Screen.Settings.route
                )

                // Use the same HomeViewModel instance for HomeScreen and SettingsScreen interactions if needed
                // But for now, SettingsScreen just triggers a refresh which HomeViewModel can handle if it's scoped correctly
                // Ideally, HomeViewModel should be scoped to the NavHost or Activity if shared.
                // Here we use the default viewModel() which is scoped to the NavBackStackEntry.
                // To share it, we can pass it explicitly or scope it to Activity.
                val homeViewModel: HomeViewModel = viewModel()

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route
                        ) {
                            composable(Screen.Home.route) {
                                HomeScreen(
                                    isDarkTheme = mainViewModel.isDarkTheme,
                                    onThemeToggle = { mainViewModel.toggleTheme() },
                                    viewModel = homeViewModel
                                )
                            }
                            composable(Screen.More.route) {
                                MoreScreen()
                            }
                            composable(Screen.Settings.route) {
                                SettingsScreen(
                                    navController = navController,
                                    isDarkTheme = mainViewModel.isDarkTheme,
                                    onThemeToggle = { mainViewModel.toggleTheme() },
                                    onColorChange = { mainViewModel.updateThemeColor(it) },
                                    mainViewModel = mainViewModel,
                                    onUpdateData = { homeViewModel.refresh() }
                                )
                            }
                            composable(Screen.About.route) {
                                AboutScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
