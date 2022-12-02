package com.example.hesapmakinesi.domain.usecase.base

import kotlinx.coroutines.flow.Flow

interface BaseUseCase<in Params, ReturnType: Any?> {
    fun invoke(params: Params): Flow<ReturnType>
}