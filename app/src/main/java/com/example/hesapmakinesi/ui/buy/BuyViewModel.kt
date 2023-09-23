package com.example.hesapmakinesi.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hesapmakinesi.data.local.SharedPreferencesManager
import com.example.hesapmakinesi.data.model.Order
import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.data.model.SavedCoins
import com.example.hesapmakinesi.domain.usecase.buy.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyViewModel @Inject constructor(
    private val buyUseCase: BuyUseCase,
    private val sharedPreferencesManager: SharedPreferencesManager,
    var preferencesName: String
) : ViewModel() {

    private val _uiEventDetail = MutableSharedFlow<BuyViewEventCoinDetail>(replay = 0)
    val uiEventDetail: SharedFlow<BuyViewEventCoinDetail> = _uiEventDetail

    fun getCoinDetail(symbol: String) {
        viewModelScope.launch {
            buyUseCase.getCoinDetail.invoke(symbol).collect {
                when (it) {
                    is GetCoinDetailState.Loading -> {
                    }
                    is GetCoinDetailState.Error -> {
                        _uiEventDetail.emit(BuyViewEventCoinDetail.ShowError(it.error))
                    }
                    is GetCoinDetailState.Success -> {
                        _uiEventDetail.emit(BuyViewEventCoinDetail.ShowData(it.data))
                    }
                }
            }
        }
    }

    fun getBuyCalculates(): ArrayList<Order> {
        return sharedPreferencesManager.getCalculatesBuy(preferencesName)
    }

    fun getNewQuantity(): String {
        return sharedPreferencesManager.getNewQuantity(preferencesName)
    }

    fun getCoinName(): String {
        return sharedPreferencesManager.getCoinName(preferencesName)
    }

    fun setBuyCalculates(hesapArrayList: ArrayList<Order>) {
        sharedPreferencesManager.setCalculatesBuy(preferencesName, hesapArrayList)
    }

    fun setNewQuantity(newQuantity: String) {
        sharedPreferencesManager.setNewQuantity(preferencesName, newQuantity)
    }

    fun setCoinName(coinName: String) {
        sharedPreferencesManager.setCoinName(preferencesName, coinName)
    }

    fun getSellCalculates(): ArrayList<Order>{
        return sharedPreferencesManager.getCalculatesSell(preferencesName)
    }
    fun setSellCalculates(hesapArrayList: ArrayList<Order>){
        sharedPreferencesManager.setCalculatesSell(preferencesName,hesapArrayList)
    }

    //update recyclerview item in savedCoinFragment
    fun getSavedCoinsList(): ArrayList<SavedCoins> {
        return sharedPreferencesManager.getSavedCoins()
    }

    fun updateSavedCoinsList(savedCoinsList: java.util.ArrayList<SavedCoins>) {
        sharedPreferencesManager.setSavedCoins(savedCoinsList)
    }
}

sealed class BuyViewEventCoinDetail {
    class ShowData(val data: CoinsResponseItem) : BuyViewEventCoinDetail()
    class ShowError(val error: String?) : BuyViewEventCoinDetail()
}