package com.example.hesapmakinesi.domain.repository.impl

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.data.remote.source.CoinRemoteDataSource
import com.example.hesapmakinesi.domain.repository.CoinsRepository
import com.example.hesapmakinesi.utils.DataState
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