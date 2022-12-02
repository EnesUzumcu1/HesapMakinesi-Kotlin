package com.example.hesapmakinesi.data.remote.source

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow


interface CoinRemoteDataSource {
    suspend fun getCoinList(): Flow<DataState<MutableList<CoinsResponseItem>>>
    suspend fun getCoinDetail(symbol: String) : Flow<DataState<CoinsResponseItem>>
}