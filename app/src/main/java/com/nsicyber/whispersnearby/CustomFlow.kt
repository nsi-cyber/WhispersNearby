package com.nsicyber.whispersnearby

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T> flowWrapper(call: suspend () -> T): Flow<Result<T>> = flow {
    try {
        val result = call()
        emit(Result.success(result))
    } catch (e: Exception) {
        Log.e("FlowWrapper", "Error occurred: ${e.message}")
        emit(Result.failure(e))
    }
}.flowOn(Dispatchers.IO)
