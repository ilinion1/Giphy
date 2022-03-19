package com.gerija.giphy.di

import com.gerija.giphy.data.api.ApiFactory
import com.gerija.giphy.data.api.ApiService
import dagger.Module
import dagger.Provides

@Module
class GifsModuleProvide {

    @Provides
    fun providesApiService(): ApiService{
        return ApiFactory.create()
    }

}