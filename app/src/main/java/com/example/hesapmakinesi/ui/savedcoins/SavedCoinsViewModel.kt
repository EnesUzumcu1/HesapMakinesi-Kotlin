package com.example.hesapmakinesi.ui.savedcoins

import androidx.lifecycle.ViewModel
import com.example.hesapmakinesi.data.local.SharedPreferencesManager
import com.example.hesapmakinesi.data.model.ListSizeControl
import com.example.hesapmakinesi.data.model.SavedCoins
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedCoinsViewModel @Inject constructor(private val sharedPreferencesManager: SharedPreferencesManager) :
    ViewModel() {

    fun getSavedCoinsList(): ArrayList<SavedCoins> {
        return sharedPreferencesManager.getSavedCoins()
    }

    fun getIDs(): ArrayList<ListSizeControl> {
        return sharedPreferencesManager.getIDs()
    }

    fun updateSavedCoinsList(savedCoinsList: java.util.ArrayList<SavedCoins>) {
        sharedPreferencesManager.setSavedCoins(savedCoinsList)
    }

    fun updateIDs(stringsID: ArrayList<ListSizeControl>) {
        sharedPreferencesManager.setIDs(stringsID)
    }

    fun deleteCoinDetail(sharedPreferencesName: String) {
        sharedPreferencesManager.deleteCoinDetail(sharedPreferencesName)
    }

    //update new quantity - delete next update
    fun getNewQuantity(preferencesName: String):String{
        return sharedPreferencesManager.getNewQuantity(preferencesName)
    }

    fun setNewQuantity(preferencesName: String,newQuantity: String){
        sharedPreferencesManager.setNewQuantity(preferencesName,newQuantity)
    }
}