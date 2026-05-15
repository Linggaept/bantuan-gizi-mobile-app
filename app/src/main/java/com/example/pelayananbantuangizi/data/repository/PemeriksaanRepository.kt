package com.example.pelayananbantuangizi.data.repository

import com.example.pelayananbantuangizi.data.api.ApiService
import com.example.pelayananbantuangizi.data.api.model.CreatePemeriksaanRequest
import com.example.pelayananbantuangizi.data.api.model.PemeriksaanDto
import com.example.pelayananbantuangizi.util.safeApiCall

class PemeriksaanRepository(private val apiService: ApiService) {

    suspend fun getPemeriksaan(lansiaId: Int): Result<List<PemeriksaanDto>> =
        safeApiCall { apiService.getPemeriksaan(lansiaId) }.mapCatching { it.data }

    suspend fun createPemeriksaan(
        lansiaId: Int,
        request: CreatePemeriksaanRequest
    ): Result<PemeriksaanDto> =
        safeApiCall { apiService.createPemeriksaan(lansiaId, request) }.mapCatching { it.data }
}
