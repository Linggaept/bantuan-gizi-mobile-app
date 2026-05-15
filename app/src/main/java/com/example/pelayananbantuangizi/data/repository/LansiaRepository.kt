package com.example.pelayananbantuangizi.data.repository

import com.example.pelayananbantuangizi.data.api.ApiService
import com.example.pelayananbantuangizi.data.api.model.*
import com.example.pelayananbantuangizi.util.safeApiCall
import okhttp3.MultipartBody

class LansiaRepository(private val apiService: ApiService) {

    suspend fun getLansia(
        nama: String? = null,
        rw: String? = null,
        kondisiKesehatan: String? = null,
        statusBantuan: String? = null,
        page: Int = 1
    ): Result<PagedResponse<LansiaDto>> =
        safeApiCall { apiService.getLansia(nama, rw, kondisiKesehatan, statusBantuan, page) }

    suspend fun getLansiaDetail(id: Int): Result<LansiaDto> =
        safeApiCall { apiService.getLansiaDetail(id) }.mapCatching { it.data }

    suspend fun createLansia(request: CreateLansiaRequest): Result<LansiaDto> =
        safeApiCall { apiService.createLansia(request) }.mapCatching { it.data }

    suspend fun updateLansia(id: Int, request: UpdateLansiaRequest): Result<LansiaDto> =
        safeApiCall { apiService.updateLansia(id, request) }.mapCatching { it.data }

    suspend fun deleteLansia(id: Int): Result<Unit> =
        safeApiCall { apiService.deleteLansia(id) }

    suspend fun uploadFotoKtp(id: Int, part: MultipartBody.Part): Result<LansiaDto> =
        safeApiCall { apiService.uploadFotoKtp(id, part) }.mapCatching { it.data }

    suspend fun getStatusBantuan(id: Int): Result<StatusBantuanDto> =
        safeApiCall { apiService.getStatusBantuan(id) }.mapCatching { it.data }
}
