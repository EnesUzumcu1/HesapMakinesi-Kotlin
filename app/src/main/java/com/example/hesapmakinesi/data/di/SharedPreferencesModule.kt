package com.example.hesapmakinesi.data.di

import android.content.Context
import com.example.hesapmakinesi.data.local.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SharedPreferencesModule {

    @Singleton
    @Provides
    fun provideSharedPreferencesManager(@ApplicationContext appContext: Context): SharedPreferencesManager =
        SharedPreferencesManager(appContext)
}