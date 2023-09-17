package com.example.hesapmakinesi.domain.usecase.coinlist

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.domain.repository.CoinsRepository
import com.example.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCoinList @Inject constructor(private val coinsRepository: CoinsRepository) {
    fun invoke(): Flow<GetCoinListState> {
        return flow {
            emit(GetCoinListState.Loading)

            coinsRepository.getCoinList().collect {
                when (it) {
                    is DataState.Loading -> {
                        emit(GetCoinListState.Loading)
                    }
                    is DataState.Error -> {
                        emit(GetCoinListState.Error(it.errorMessage))
                    }
                    is DataState.Success -> {
                        emit(GetCoinListState.Success(it.data))
                    }
                }
            }
        }
    }
}

sealed class GetCoinListState {
    data class Success(val data: MutableList<CoinsResponseItem>) : GetCoinListState()
    data class Error(val error: String?) : GetCoinListState()
    object Loading : GetCoinListState()
}