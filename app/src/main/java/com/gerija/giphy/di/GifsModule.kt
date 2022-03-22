package com.gerija.giphy.di

import com.gerija.giphy.data.repository.GifsRepositoryImpl
import com.gerija.giphy.domain.GifsRepository
import com.gerija.giphy.presentation.SingleGifActivity
import dagger.Binds
import dagger.Module

@Module
interface GifsModule {

    @ApplicationScope
    @Binds
    fun bindsRepository(impl: GifsRepositoryImpl): GifsRepository
}