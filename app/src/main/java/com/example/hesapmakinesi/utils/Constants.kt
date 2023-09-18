package com.example.hesapmakinesi.utils

class Constants {
    companion object {
        const val BASE_URL = "https://api1.binance.com/api/v3/"

        //Api Endpoint
        const val COIN_LIST = "ticker/price"

        //SharedPreferences
        const val PREFERENCE_NAME = "Liste"
        const val COIN_DETAIL_1 = "Note1"
        const val COIN_DETAIL_2 = "Note2"
        const val COIN_DETAIL_3 = "Note3"
        const val COIN_DETAIL_4 = "Note4"
        const val COIN_DETAIL_5 = "Note5"
        const val COIN_DETAIL_6 = "Note6"
        const val COIN_DETAIL_7 = "Note7"
        const val COIN_DETAIL_8 = "Note8"
        const val COIN_DETAIL_9 = "Note9"
        const val COIN_DETAIL_10 = "Note10"
        const val COIN_DETAIL_11 = "Note11"
        const val COIN_DETAIL_12 = "Note12"
        const val COIN_DETAIL_13 = "Note13"
        const val COIN_DETAIL_14 = "Note14"
        const val COIN_DETAIL_15 = "Note15"
        const val COIN_DETAIL_16 = "Note16"
        const val COIN_DETAIL_17 = "Note17"
        const val COIN_DETAIL_18 = "Note18"
        const val COIN_DETAIL_19 = "Note19"
        const val COIN_DETAIL_20 = "Note20"

        const val PREF_LISTS = "listeler"
        const val PREF_IDS = "IDler"

        const val COIN_DETAIL_CALCULATES_BUY = "hesaplar"
        const val COIN_DETAIL_NEW_QUANTITY = "yeniAdet"
        const val COIN_DETAIL_COIN_NAME = "coinAdi"

        const val COIN_DETAIL_CALCULATES_SELL = "hesaplarSatis"

        const val DEFAULT_NEW_QUANTITY_TEXT = "Yeni adet için tıkla"
        const val DEFAULT_COIN_NAME_TEXT = "BTCUSDT"
        const val DEFAULT_UNSELECTED_COIN_NAME_TEXT = "Eklemek için tıkla"

        const val ALERT_DIALOG_DELETE_TITLE = "Kaydı Sil"
        const val ALERT_DIALOG_DELETE_MESSAGE = "Kalıcı olarak silmek istediğinizden emin misin?"

        const val SEND_PREF_NAME = "prefName"
        const val WRONG_INPUT_ERROR = "Kabul edilmeyen değer"
        const val NO_INTERNET_ERROR = "İnternet bağlantısı bulunamadı!"
        const val PROFIT_COULD_NOT_BE_CALCULATED_ERROR = "Kâr oranı hesaplanması için internet bağlantısı gerekir"

        const val SAVED_STATE_HANDLE_KEY_ORDER = "order"
        const val SAVED_STATE_HANDLE_KEY_NEW_AMOUNT = "newAmount"
        const val SAVED_STATE_HANDLE_KEY_COIN = "coin"
        const val SAVED_STATE_HANDLE_KEY_CLOSED_BOTTOM_SHEET = ""
    }
}