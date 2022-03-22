package com.gerija.giphy.presentation

import android.app.Application
import com.gerija.giphy.di.DaggerApplicationsComponent
import com.gerija.giphy.di.GifsModuleProvide
import javax.inject.Inject

class MyApplication: Application() {

    val component by lazy {
        DaggerApplicationsComponent.builder().gifsModuleProvide(GifsModuleProvide(this)).build()
    }
}