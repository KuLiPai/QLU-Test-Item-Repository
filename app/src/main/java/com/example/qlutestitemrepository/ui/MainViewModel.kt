package com.example.qlutestitemrepository.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

enum class DataSource {
    GITHUB,
    CLOUDFLARE_R2
}

class MainViewModel : ViewModel() {
    var isDarkTheme by mutableStateOf(false)
    var themeColor by mutableStateOf<Color?>(null)
    
    var dataSource by mutableStateOf(DataSource.GITHUB)
        private set

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }

    fun updateThemeColor(color: Color) {
        themeColor = color
    }
    
    fun updateDataSource(source: DataSource) {
        dataSource = source
    }
}
