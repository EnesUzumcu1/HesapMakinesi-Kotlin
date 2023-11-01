package com.enesuzumcu.hesapmakinesi.domain.usecase.coinlist

import javax.inject.Inject

data class CoinListUseCase @Inject constructor(
    val getCoinList: GetCoinList
)
