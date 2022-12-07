package com.example.hesapmakinesi.domain.usecase.buy

import javax.inject.Inject

data class BuyUseCase @Inject constructor(
    val getCoinList: GetCoinList,
    val getCoinDetail: GetCoinDetail
)
