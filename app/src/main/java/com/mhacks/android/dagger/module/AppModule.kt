package com.mhacks.android.dagger.module

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule constructor(application: Application){

    var application: Application

    init {
        this.application = application
    }

    @Provides
    @Singleton
    fun provideApplication(): Application {
        return application
    }


}