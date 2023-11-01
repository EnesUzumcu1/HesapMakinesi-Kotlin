package com.enesuzumcu.hesapmakinesi.data.di

import com.enesuzumcu.hesapmakinesi.data.remote.api.CoinApiService
import com.enesuzumcu.hesapmakinesi.data.remote.source.CoinRemoteDataSource
import com.enesuzumcu.hesapmakinesi.data.remote.source.impl.CoinRemoteDataSourceImpl
import com.enesuzumcu.hesapmakinesi.domain.repository.CoinsRepository
import com.enesuzumcu.hesapmakinesi.domain.repository.impl.CoinsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class CoinsModule {

    @Singleton
    @Provides
    fun provideCoinsService(retrofit: Retrofit) = retrofit.create(CoinApiService::class.java)

    @Singleton
    @Provides
    fun provideCoinsRemoteDataSource(coinsService: CoinApiService): CoinRemoteDataSource =
        CoinRemoteDataSourceImpl(coinsService)

    @Singleton
    @Provides
    fun provideCoinsRepository(coinRemoteDataSource: CoinRemoteDataSource): CoinsRepository =
        CoinsRepositoryImpl(coinRemoteDataSource)
}