package com.gerija.giphy.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gerija.giphy.di.ApplicationScope
import com.gerija.giphy.domain.GetSearchGifsUseCase
import com.gerija.giphy.domain.GetTopGifsUseCase
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class GifsViewModelFactory @Inject constructor(
    private val viewModelsProvider: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelsProvider[modelClass]?.get() as T
    }
}