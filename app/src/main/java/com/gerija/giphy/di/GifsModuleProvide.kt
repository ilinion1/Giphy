package com.gerija.giphy.di

import android.app.Application
import android.content.Context
import com.gerija.giphy.data.api.ApiFactory
import com.gerija.giphy.data.api.ApiService
import com.gerija.giphy.data.database.GifsDao
import com.gerija.giphy.data.database.GifsDatabase
import dagger.Module
import dagger.Provides

@Module
class GifsModuleProvide(private val application: Application) {

    @Provides
    fun providesApiService(): ApiService{
        return ApiFactory.create()
    }

    @Provides
    fun providesContext(): Application {
        return application
    }

    @Provides
    fun providesDao(): GifsDao{
        return GifsDatabase.getInstance(application).gifsDao()
    }

}