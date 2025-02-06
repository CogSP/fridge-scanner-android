// FridgeViewModelFactory.kt
package com.example.fridgescanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fridgescanner.data.FridgeRepository
import com.example.fridgescanner.pythonanywhereAPI.AuthService

class FridgeViewModelFactory(
    private val repository: FridgeRepository,
    private val authService: AuthService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FridgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FridgeViewModel(repository, authService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
