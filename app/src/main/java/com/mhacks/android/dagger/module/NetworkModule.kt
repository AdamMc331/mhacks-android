package com.mhacks.android.dagger.module

import dagger.Module
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import javax.inject.Singleton
import dagger.Provides



/**
 * Created by jeffreychang on 8/2/17.
 */
@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }


}