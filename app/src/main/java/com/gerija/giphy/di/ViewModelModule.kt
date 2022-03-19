package com.gerija.giphy.di

import androidx.lifecycle.ViewModel
import com.gerija.giphy.presentation.GifsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(GifsViewModel::class)
    @Binds
    fun bindsGifsViewModel(impl: GifsViewModel): ViewModel
}