package com.example.hesapmakinesi.domain.repository

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow

interface CoinsRepository {
    suspend fun getCoinList(): Flow<DataState<MutableList<CoinsResponseItem>>>
    suspend fun getCoinDetail(symbol: String) : Flow<DataState<CoinsResponseItem>>
}