package com.example.hesapmakinesi.domain.usecase.buy

import javax.inject.Inject

data class BuyUseCase @Inject constructor(
    val getCoinDetail: GetCoinDetail
)
