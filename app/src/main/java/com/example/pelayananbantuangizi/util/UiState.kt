package com.example.pelayananbantuangizi.util

import retrofit2.Response
import java.io.IOException

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class ApiException(val code: Int, message: String) : Exception(message)

suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                Result.success(@Suppress("UNCHECKED_CAST") Unit as T)
            }
        } else {
            val errorMsg = when (response.code()) {
                401 -> "UNAUTHORIZED"
                403 -> "Tidak punya akses"
                404 -> "Data tidak ditemukan"
                422 -> "Validasi gagal"
                else -> "Error ${response.code()}"
            }
            Result.failure(ApiException(response.code(), errorMsg))
        }
    } catch (e: IOException) {
        Result.failure(Exception("Tidak ada koneksi internet"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
