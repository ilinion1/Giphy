package com.gerija.giphy

import android.app.Application
import com.gerija.giphy.di.DaggerApplicationsComponent

class MyApplication: Application() {

    val component by lazy {
        DaggerApplicationsComponent.create()
    }
}