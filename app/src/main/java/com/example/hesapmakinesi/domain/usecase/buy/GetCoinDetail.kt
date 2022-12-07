package com.example.hesapmakinesi.domain.usecase.buy

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.domain.repository.CoinsRepository
import com.example.hesapmakinesi.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCoinDetail @Inject constructor(private val coinsRepository: CoinsRepository) {
    fun invoke(symbol: String): Flow<GetCoinDetailState> {
        return flow {
            emit(GetCoinDetailState.Loading)

            coinsRepository.getCoinDetail(symbol).collect {
                when (it) {
                    is DataState.Loading -> {
                        emit(GetCoinDetailState.Loading)
                    }
                    is DataState.Error -> {
                        emit(GetCoinDetailState.Error(it.errorMessage))
                    }
                    is DataState.Success -> {
                        emit(GetCoinDetailState.Success((it.data)))
                    }
                }
            }
        }
    }
}

sealed class GetCoinDetailState {
    data class Success(val data: CoinsResponseItem) : GetCoinDetailState()
    data class Error(val error: String?) : GetCoinDetailState()
    object Loading : GetCoinDetailState()
}