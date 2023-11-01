package com.enesuzumcu.hesapmakinesi.data.model

import java.math.BigDecimal

data class Order(
    var adet: BigDecimal,
    var fiyat: BigDecimal
)