package com.enesuzumcu.hesapmakinesi.domain.usecase.buy

import javax.inject.Inject

data class BuyUseCase @Inject constructor(
    val getCoinDetail: GetCoinDetail
)
