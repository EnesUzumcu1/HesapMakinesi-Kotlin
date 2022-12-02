package com.example.hesapmakinesi.data.remote.source.impl

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.data.remote.api.CoinApiService
import com.example.hesapmakinesi.data.remote.source.CoinRemoteDataSource
import com.example.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinRemoteDataSourceImpl @Inject constructor(private val coinApiService: CoinApiService) :
    BaseRemoteDataSource(), CoinRemoteDataSource {
    override suspend fun getCoinList(): Flow<DataState<MutableList<CoinsResponseItem>>> {
        return getResult { coinApiService.getCoinList() }
    }

    override suspend fun getCoinDetail(symbol: String): Flow<DataState<CoinsResponseItem>> {
        return getResult { coinApiService.getCoinDetail(symbol) }
    }
}