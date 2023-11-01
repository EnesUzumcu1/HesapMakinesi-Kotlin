package com.enesuzumcu.hesapmakinesi.domain.repository

import com.enesuzumcu.hesapmakinesi.data.model.CoinsResponseItem
import com.enesuzumcu.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow

interface CoinsRepository {
    suspend fun getCoinList(): Flow<DataState<MutableList<CoinsResponseItem>>>
    suspend fun getCoinDetail(symbol: String) : Flow<DataState<CoinsResponseItem>>
}