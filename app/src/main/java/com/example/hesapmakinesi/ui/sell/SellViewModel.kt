package com.example.hesapmakinesi.ui.sell

import androidx.lifecycle.ViewModel
import com.example.hesapmakinesi.data.local.SharedPreferencesManager
import com.example.hesapmakinesi.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SellViewModel @Inject constructor(private val sharedPreferencesManager: SharedPreferencesManager) : ViewModel() {

    fun getCalculates(preferencesName: String): ArrayList<Order>{
        return sharedPreferencesManager.getCalculatesSell(preferencesName)
    }
    fun setCalculates(preferencesName: String,hesapArrayList: ArrayList<Order>){
        sharedPreferencesManager.setCalculatesSell(preferencesName,hesapArrayList)
    }
}