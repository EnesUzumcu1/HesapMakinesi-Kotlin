package com.example.hesapmakinesi.data.remote.source.impl

import com.example.hesapmakinesi.data.model.ApiError
import com.example.hesapmakinesi.utils.DataState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Response

open class BaseRemoteDataSource {
    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Flow<DataState<T>> {
        return flow<DataState<T>> {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) emit(DataState.Success(body))
                else {
                    val apiError: ApiError =
                        Gson().fromJson(response.errorBody()?.charStream(), ApiError::class.java)
                    emit(DataState.Error(apiError.msg))
                }
            } else {
                val apiError: ApiError =
                    Gson().fromJson(response.errorBody()?.charStream(), ApiError::class.java)
                emit(DataState.Error(apiError.msg))
            }

        }
            .catch {
                emit(DataState.Error(ApiError(-1, it.message ?: it.toString()).msg))
            }
            .onStart { emit(DataState.Loading()) }
            .flowOn(Dispatchers.IO)
    }
}