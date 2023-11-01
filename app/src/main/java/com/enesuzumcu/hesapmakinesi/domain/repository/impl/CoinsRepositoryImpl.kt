package com.enesuzumcu.hesapmakinesi.domain.repository.impl

import com.enesuzumcu.hesapmakinesi.data.model.CoinsResponseItem
import com.enesuzumcu.hesapmakinesi.data.remote.source.CoinRemoteDataSource
import com.enesuzumcu.hesapmakinesi.domain.repository.CoinsRepository
import com.enesuzumcu.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoinsRepositoryImpl @Inject constructor(private val coinsRemoteDataSource: CoinRemoteDataSource) : CoinsRepository {
    override suspend fun getCoinList(): Flow<DataState<MutableList<CoinsResponseItem>>> {
        return coinsRemoteDataSource.getCoinList()
    }

    override suspend fun getCoinDetail(symbol: String): Flow<DataState<CoinsResponseItem>> {
        return coinsRemoteDataSource.getCoinDetail(symbol)
    }
}