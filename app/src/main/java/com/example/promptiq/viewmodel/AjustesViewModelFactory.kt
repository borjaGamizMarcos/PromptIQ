package com.example.promptiq.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AjustesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AjustesViewModel::class.java)) {
            return AjustesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
