package com.triline.billionlights.model.retrofit

import kotlinx.coroutines.Deferred

data class ApiBundle(val networkApi: NetworkApi)

class NetworkApiProvider(private val networkApi: NetworkApi) {

    suspend fun <T> makeCall(block: (ApiBundle) -> Deferred<T>): T {

        val bundle = ApiBundle(networkApi)

        return try {
            block(bundle).networkCall()
        } catch (e: Exception) {
            throw e
        }
    }
}

suspend fun <T> Deferred<T>.networkCall(): T {
    try {
        return this.await()
    } catch (e: Exception) {
        throw Exception(e)
    }
}