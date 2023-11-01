package com.enesuzumcu.hesapmakinesi.data.remote.source

import com.enesuzumcu.hesapmakinesi.data.model.CoinsResponseItem
import com.enesuzumcu.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow


interface CoinRemoteDataSource {
    suspend fun getCoinList(): Flow<DataState<MutableList<CoinsResponseItem>>>
    suspend fun getCoinDetail(symbol: String) : Flow<DataState<CoinsResponseItem>>
}