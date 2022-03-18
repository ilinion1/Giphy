package com.gerija.giphy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gerija.giphy.data.repository.GifsRepositoryImpl

class GifsViewModelFactory(private val repository: GifsRepositoryImpl): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GifsViewModel(repository) as T
    }
}