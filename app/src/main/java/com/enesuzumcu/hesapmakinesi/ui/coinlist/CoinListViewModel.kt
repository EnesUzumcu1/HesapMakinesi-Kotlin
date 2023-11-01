package com.enesuzumcu.hesapmakinesi.ui.coinlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesuzumcu.hesapmakinesi.data.model.CoinsResponseItem
import com.enesuzumcu.hesapmakinesi.domain.usecase.coinlist.CoinListUseCase
import com.enesuzumcu.hesapmakinesi.domain.usecase.coinlist.GetCoinListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinListViewModel @Inject constructor(
    private val coinListUseCase: CoinListUseCase
) : ViewModel(){

    private val _uiState = MutableStateFlow<CoinListUiState>(CoinListUiState.Empty)
    val uiState: StateFlow<CoinListUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<CoinListViewEvent>(replay = 0)
    val uiEvent: SharedFlow<CoinListViewEvent> = _uiEvent

    fun getCoinList() {
        viewModelScope.launch {
            coinListUseCase.getCoinList.invoke().collect {
                when (it) {
                    is GetCoinListState.Loading -> {
                        _uiState.value = CoinListUiState.Loading
                    }
                    is GetCoinListState.Error -> {
                        _uiState.value = CoinListUiState.Empty
                        _uiEvent.emit(CoinListViewEvent.ShowError(it.error))
                    }
                    is GetCoinListState.Success -> {
                        _uiState.value = CoinListUiState.Empty

                        it.data.filter {
                            it.symbol?.contains("USDT") == true
                        }.sortedBy {
                            it.symbol
                        }.toMutableList().apply {
                            _uiEvent.emit(CoinListViewEvent.ShowData(this))
                        }
                    }
                }
            }
        }
    }
}
sealed class CoinListViewEvent {
    class ShowData(val data: MutableList<CoinsResponseItem>) : CoinListViewEvent()
    class ShowError(val error: String?) : CoinListViewEvent()
}

sealed class CoinListUiState {
    object Empty : CoinListUiState()
    object Loading : CoinListUiState()
}