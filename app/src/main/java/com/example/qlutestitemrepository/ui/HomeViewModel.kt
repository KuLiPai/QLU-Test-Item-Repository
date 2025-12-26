package com.example.qlutestitemrepository.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qlutestitemrepository.data.model.GitHubFileItem
import com.example.qlutestitemrepository.data.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var fileItems by mutableStateOf<List<GitHubFileItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // Track current path stack for navigation
    private val pathStack = ArrayDeque<String>()
    var currentPath by mutableStateOf("")
        private set

    init {
        loadContents("")
    }

    fun loadContents(path: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                fileItems = RetrofitClient.instance.getContents(path)
                currentPath = path
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    fun navigateTo(item: GitHubFileItem) {
        if (item.type == "dir") {
            pathStack.addLast(currentPath)
            loadContents(item.path)
        } else {
            // Handle file click (download or open)
            // For now, maybe just log or show toast (not implemented here)
        }
    }

    fun navigateBack(): Boolean {
        if (pathStack.isNotEmpty()) {
            val previousPath = pathStack.removeLast()
            loadContents(previousPath)
            return true
        }
        return false
    }
    
    fun refresh() {
        loadContents(currentPath)
    }
}
