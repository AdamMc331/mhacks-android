package com.mhacks.android.dagger.module.component

import com.mhacks.android.dagger.module.NetworkModule
import com.mhacks.android.dagger.module.AppModule
import com.mhacks.android.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by jeffreychang on 8/2/17.
 */
@Singleton
@Component(modules = arrayOf(
        NetworkModule::class,
        AppModule::class
))
interface NetComponent {
    fun inject(activity: MainActivity)
}