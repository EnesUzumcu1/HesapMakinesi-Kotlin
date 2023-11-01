package com.enesuzumcu.hesapmakinesi.data.local

import android.content.Context
import android.content.SharedPreferences
import com.enesuzumcu.hesapmakinesi.data.model.Order
import com.enesuzumcu.hesapmakinesi.data.model.ListSizeControl
import com.enesuzumcu.hesapmakinesi.data.model.SavedCoins
import com.enesuzumcu.hesapmakinesi.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) {

    private val pref: SharedPreferences =
        context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    private val coinDetail1: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_1, Context.MODE_PRIVATE)
    private val coinDetail2: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_2, Context.MODE_PRIVATE)
    private val coinDetail3: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_3, Context.MODE_PRIVATE)
    private val coinDetail4: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_4, Context.MODE_PRIVATE)
    private val coinDetail5: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_5, Context.MODE_PRIVATE)
    private val coinDetail6: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_6, Context.MODE_PRIVATE)
    private val coinDetail7: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_7, Context.MODE_PRIVATE)
    private val coinDetail8: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_8, Context.MODE_PRIVATE)
    private val coinDetail9: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_9, Context.MODE_PRIVATE)
    private val coinDetail10: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_10, Context.MODE_PRIVATE)
    private val coinDetail11: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_11, Context.MODE_PRIVATE)
    private val coinDetail12: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_12, Context.MODE_PRIVATE)
    private val coinDetail13: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_13, Context.MODE_PRIVATE)
    private val coinDetail14: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_14, Context.MODE_PRIVATE)
    private val coinDetail15: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_15, Context.MODE_PRIVATE)
    private val coinDetail16: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_16, Context.MODE_PRIVATE)
    private val coinDetail17: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_17, Context.MODE_PRIVATE)
    private val coinDetail18: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_18, Context.MODE_PRIVATE)
    private val coinDetail19: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_19, Context.MODE_PRIVATE)
    private val coinDetail20: SharedPreferences =
        context.getSharedPreferences(Constants.COIN_DETAIL_20, Context.MODE_PRIVATE)
    private val editor = pref.edit()
    private val gson = Gson()

    fun getSavedCoins(): ArrayList<SavedCoins> {
        val json = pref.getString(Constants.PREF_LISTS, null)
        val type = object : TypeToken<java.util.ArrayList<SavedCoins?>?>() {}.type
        json?.let {
            type?.let {
                return gson.fromJson<java.util.ArrayList<SavedCoins>>(json, type)
            }
        }
        return java.util.ArrayList<SavedCoins>()
    }

    fun getIDs(): ArrayList<ListSizeControl> {
        val json = pref.getString(Constants.PREF_IDS, null)
        val type = object : TypeToken<java.util.ArrayList<ListSizeControl?>?>() {}.type
        json?.let {
            type?.let {
                return gson.fromJson<java.util.ArrayList<ListSizeControl>>(json, type)
            }
        }
        return java.util.ArrayList()
    }

    fun setSavedCoins(kayitliCoinlers: ArrayList<SavedCoins>) {
        val json = gson.toJson(kayitliCoinlers)
        editor.putString(Constants.PREF_LISTS, json)
        editor.apply()
    }

    fun setIDs(stringsID: ArrayList<ListSizeControl>) {
        val json = gson.toJson(stringsID)
        editor.putString(Constants.PREF_IDS, json)
        editor.apply()
    }

    private fun getSharedPreferences(noteX: String): SharedPreferences {
        when (noteX) {
            Constants.COIN_DETAIL_1 -> {
                return coinDetail1
            }
            Constants.COIN_DETAIL_2 -> {
                return coinDetail2
            }
            Constants.COIN_DETAIL_3 -> {
                return coinDetail3
            }
            Constants.COIN_DETAIL_4 -> {
                return coinDetail4
            }
            Constants.COIN_DETAIL_5 -> {
                return coinDetail5
            }
            Constants.COIN_DETAIL_6 -> {
                return coinDetail6
            }
            Constants.COIN_DETAIL_7 -> {
                return coinDetail7
            }
            Constants.COIN_DETAIL_8 -> {
                return coinDetail8
            }
            Constants.COIN_DETAIL_9 -> {
                return coinDetail9
            }
            Constants.COIN_DETAIL_10 -> {
                return coinDetail10
            }
            Constants.COIN_DETAIL_11 -> {
                return coinDetail11
            }
            Constants.COIN_DETAIL_12 -> {
                return coinDetail12
            }
            Constants.COIN_DETAIL_13 -> {
                return coinDetail13
            }
            Constants.COIN_DETAIL_14 -> {
                return coinDetail14
            }
            Constants.COIN_DETAIL_15 -> {
                return coinDetail15
            }
            Constants.COIN_DETAIL_16 -> {
                return coinDetail16
            }
            Constants.COIN_DETAIL_17 -> {
                return coinDetail17
            }
            Constants.COIN_DETAIL_18 -> {
                return coinDetail18
            }
            Constants.COIN_DETAIL_19 -> {
                return coinDetail19
            }
            Constants.COIN_DETAIL_20 -> {
                return coinDetail20
            }
            else -> return pref
        }
    }

    fun getCalculatesBuy(sharedPreferencesName: String): ArrayList<Order> {
        getSharedPreferences(sharedPreferencesName).apply {
            val json = this.getString(Constants.COIN_DETAIL_CALCULATES_BUY, null)
            val type = object : TypeToken<ArrayList<Order?>?>() {}.type
            json?.let {
                type?.let {
                    return gson.fromJson<ArrayList<Order>>(json, type)
                }
            }
        }
        return ArrayList<Order>()
    }

    fun getNewQuantity(sharedPreferencesName: String): String {
        getSharedPreferences(sharedPreferencesName).apply {
            return this.getString(
                Constants.COIN_DETAIL_NEW_QUANTITY,
                Constants.DEFAULT_NEW_QUANTITY_TEXT
            ).toString()
        }
    }

    fun getCoinName(sharedPreferencesName: String): String {
        getSharedPreferences(sharedPreferencesName).apply {
            return this.getString(Constants.COIN_DETAIL_COIN_NAME, Constants.DEFAULT_COIN_NAME_TEXT)
                .toString()
        }
    }

    fun setCalculatesBuy(sharedPreferencesName: String, hesapArrayList: ArrayList<Order>) {
        getSharedPreferences(sharedPreferencesName).apply {
            val editor = this.edit()
            val json = gson.toJson(hesapArrayList)
            editor.putString(Constants.COIN_DETAIL_CALCULATES_BUY, json)
            editor.apply()
        }
    }

    fun setNewQuantity(sharedPreferencesName: String, newQuantity: String) {
        getSharedPreferences(sharedPreferencesName).apply {
            val editor = this.edit()
            editor.putString(Constants.COIN_DETAIL_NEW_QUANTITY, newQuantity)
            editor.apply()
        }
    }

    fun setCoinName(sharedPreferencesName: String, coinName: String) {
        getSharedPreferences(sharedPreferencesName).apply {
            val editor = this.edit()
            editor.putString(Constants.COIN_DETAIL_COIN_NAME, coinName)
            editor.apply()
        }
    }

    fun getCalculatesSell(sharedPreferencesName: String): ArrayList<Order> {
        getSharedPreferences(sharedPreferencesName).apply {
            val json = this.getString(Constants.COIN_DETAIL_CALCULATES_SELL, null)
            val type = object : TypeToken<ArrayList<Order?>?>() {}.type
            json?.let {
                type?.let {
                    return gson.fromJson<ArrayList<Order>>(json, type)
                }
            }
        }
        return ArrayList<Order>()
    }

    fun setCalculatesSell(sharedPreferencesName: String, hesapArrayList: ArrayList<Order>) {
        getSharedPreferences(sharedPreferencesName).apply {
            val editor = this.edit()
            val json = gson.toJson(hesapArrayList)
            editor.putString(Constants.COIN_DETAIL_CALCULATES_SELL, json)
            editor.apply()
        }
    }

    fun deleteCoinDetail(sharedPreferencesName: String) {
        getSharedPreferences(sharedPreferencesName).apply {
            val editor = this.edit()
            editor.clear().apply()
        }
    }
}