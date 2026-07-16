package com.example.pelayananbantuangizi.data.repository

import com.example.pelayananbantuangizi.data.api.ApiService
import com.example.pelayananbantuangizi.data.api.model.CreatePemeriksaanRequest
import com.example.pelayananbantuangizi.data.api.model.MonitoringEntryDto
import com.example.pelayananbantuangizi.data.api.model.PemeriksaanDto
import com.example.pelayananbantuangizi.util.safeApiCall

class PemeriksaanRepository(private val apiService: ApiService) {

    suspend fun createPemeriksaan(
        lansiaId: Int,
        request: CreatePemeriksaanRequest
    ): Result<PemeriksaanDto> =
        safeApiCall { apiService.createPemeriksaan(lansiaId, request) }.mapCatching { it.data }

    suspend fun getMonitoring(lansiaId: Int): Result<List<MonitoringEntryDto>> =
        safeApiCall { apiService.getMonitoring(lansiaId) }.mapCatching { it.data }
}
