package com.example.hesapmakinesi.domain.usecase.buy

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.domain.repository.CoinsRepository
import com.example.hesapmakinesi.domain.usecase.base.BaseUseCase
import com.example.hesapmakinesi.utils.DataState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class BuyUseCase @Inject constructor(private val coinsRepository: CoinsRepository) :
    BaseUseCase<BuyUseCaseParams, BuyUseCaseState> {
    override fun invoke(params: BuyUseCaseParams): Flow<BuyUseCaseState> {
        return flow {
            emit(BuyUseCaseState.Loading)

            getCoinList(params.processType, params.symbol).collect {
                emit(it)
            }

        }
    }

    private fun getCoinList(processType: Int, symbol: String) = callbackFlow {
        when (processType) {
            1 -> {
                val callback = coinsRepository.getCoinList().collect {
                    when (it) {
                        is DataState.Loading -> {
                            trySendBlocking(BuyUseCaseState.Loading)
                        }
                        is DataState.Error -> {
                            trySendBlocking(BuyUseCaseState.Error(it.errorMessage))
                        }
                        is DataState.Success -> {
                            trySendBlocking(BuyUseCaseState.Success(it.data))
                        }
                    }
                }
                awaitClose { callback }
            }
            2 -> {
                val callback = coinsRepository.getCoinDetail(symbol).collect {
                    when (it) {
                        is DataState.Loading -> {
                            trySendBlocking(BuyUseCaseState.Loading)
                        }
                        is DataState.Error -> {
                            trySendBlocking(BuyUseCaseState.Error(it.errorMessage))
                        }
                        is DataState.Success -> {
                            trySendBlocking(
                                BuyUseCaseState.Success(
                                    mutableListOf<CoinsResponseItem>(
                                        (it.data)
                                    )
                                )
                            )
                        }
                    }
                }
                awaitClose { callback }
            }
        }
    }

}


class BuyUseCaseParams(
    val processType: Int,
    val symbol: String
)

sealed class BuyUseCaseState {
    data class Success(val data: MutableList<CoinsResponseItem>) : BuyUseCaseState()
    data class Error(val error: String?) : BuyUseCaseState()
    object Loading : BuyUseCaseState()
}
