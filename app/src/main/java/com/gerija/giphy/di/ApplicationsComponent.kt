package com.gerija.giphy.di



import com.gerija.giphy.presentation.MainActivity
import com.gerija.giphy.presentation.SingleGifActivity
import dagger.Component

@ApplicationScope
@Component(modules = [GifsModule::class, GifsModuleProvide::class, ViewModelModule::class])
interface ApplicationsComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: SingleGifActivity)
}