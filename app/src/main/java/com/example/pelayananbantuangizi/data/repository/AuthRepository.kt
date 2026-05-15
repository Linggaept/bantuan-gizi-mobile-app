package com.example.pelayananbantuangizi.data.repository

import com.example.pelayananbantuangizi.data.api.ApiService
import com.example.pelayananbantuangizi.data.api.model.LoginRequest
import com.example.pelayananbantuangizi.data.api.model.UserDto
import com.example.pelayananbantuangizi.local.TokenDataStore
import com.example.pelayananbantuangizi.util.safeApiCall

class AuthRepository(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore
) {
    suspend fun login(email: String, password: String): Result<UserDto> {
        val result = safeApiCall { apiService.login(LoginRequest(email, password)) }
        return result.mapCatching { response ->
            if (response.data.user.role != "operator") {
                throw Exception("Akses ditolak. Hanya operator yang dapat masuk.")
            }
            tokenDataStore.saveToken(response.data.token)
            response.data.user
        }
    }

    suspend fun logout(): Result<Unit> {
        val result = safeApiCall { apiService.logout() }
        tokenDataStore.clearToken()
        return result
    }
}
