package com.example.hesapmakinesi.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hesapmakinesi.data.local.SharedPreferencesManager
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.data.model.SavedCoins
import com.example.hesapmakinesi.domain.usecase.buy.BuyUseCase
import com.example.hesapmakinesi.domain.usecase.buy.BuyUseCaseParams
import com.example.hesapmakinesi.domain.usecase.buy.BuyUseCaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BuyViewModel @Inject constructor(
    private val buyUseCase: BuyUseCase,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<BuyUiState>(BuyUiState.Empty)
    val uiState: StateFlow<BuyUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<BuyViewEvent>(replay = 0)
    val uiEvent: SharedFlow<BuyViewEvent> = _uiEvent

    private val _uiEventDetail = MutableSharedFlow<BuyViewEventCoinDetail>(replay = 0)
    val uiEventDetail: SharedFlow<BuyViewEventCoinDetail> = _uiEventDetail

    fun getCoinList() {
        viewModelScope.launch {
            buyUseCase.invoke(BuyUseCaseParams(1, "")).collect {
                when (it) {
                    is BuyUseCaseState.Loading -> {
                        _uiState.value = BuyUiState.Loading
                    }
                    is BuyUseCaseState.Error -> {
                        _uiState.value = BuyUiState.Empty
                        _uiEvent.emit(BuyViewEvent.ShowError(it.error))
                    }
                    is BuyUseCaseState.Success -> {
                        _uiState.value = BuyUiState.Empty

                        it.data.filter {
                            it.symbol?.contains("USDT") == true
                        }.sortedBy {
                            it.symbol
                        }.toMutableList().apply {
                            _uiEvent.emit(BuyViewEvent.ShowData(this))
                        }
                    }
                }
            }
        }
    }

    fun getCoinDetail(symbol: String) {
        viewModelScope.launch {

            buyUseCase.invoke(BuyUseCaseParams(2, symbol)).collect {
                when (it) {
                    is BuyUseCaseState.Loading -> {
                    }
                    is BuyUseCaseState.Error -> {
                        _uiEventDetail.emit(BuyViewEventCoinDetail.ShowError(it.error))
                    }
                    is BuyUseCaseState.Success -> {
                        _uiEventDetail.emit(BuyViewEventCoinDetail.ShowData(it.data[0]))
                    }
                }
            }
        }
    }

    fun getCalculates(preferencesName: String): ArrayList<Order>{
        return sharedPreferencesManager.getCalculatesBuy(preferencesName)
    }
    fun getNewQuantity(preferencesName: String):String{
        return sharedPreferencesManager.getNewQuantity(preferencesName)
    }
    fun getCoinName(preferencesName: String):String{
        return sharedPreferencesManager.getCoinName(preferencesName)
    }
    fun setCalculates(preferencesName: String,hesapArrayList: ArrayList<Order>){
        sharedPreferencesManager.setCalculatesBuy(preferencesName,hesapArrayList)
    }
    fun setNewQuantity(preferencesName: String,newQuantity: String){
        sharedPreferencesManager.setNewQuantity(preferencesName,newQuantity)
    }
    fun setCoinName(preferencesName: String,coinName:String){
        sharedPreferencesManager.setCoinName(preferencesName,coinName)
    }
    //update recyclerview item in savedCoinFragment
    fun getSavedCoinsList(): ArrayList<SavedCoins> {
        return sharedPreferencesManager.getSavedCoins()
    }
    fun updateSavedCoinsList(savedCoinsList: java.util.ArrayList<SavedCoins>) {
        sharedPreferencesManager.setSavedCoins(savedCoinsList)
    }
}

sealed class BuyViewEvent {
    class ShowData(val data: MutableList<CoinsResponseItem>) : BuyViewEvent()
    class ShowError(val error: String?) : BuyViewEvent()
}
sealed class BuyViewEventCoinDetail {
    class ShowData(val data: CoinsResponseItem) : BuyViewEventCoinDetail()
    class ShowError(val error: String?) : BuyViewEventCoinDetail()
}

sealed class BuyUiState {
    object Empty : BuyUiState()
    object Loading : BuyUiState()
}