package com.example.hesapmakinesi.data.remote.api

import com.example.hesapmakinesi.data.model.CoinsResponseItem
import com.example.hesapmakinesi.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApiService {
    @GET(Constants.COIN_LIST)
    suspend fun getCoinList(): Response<MutableList<CoinsResponseItem>>

    @GET(Constants.COIN_LIST)
    suspend fun getCoinDetail(@Query("symbol") symbol: String) : Response<CoinsResponseItem>
}