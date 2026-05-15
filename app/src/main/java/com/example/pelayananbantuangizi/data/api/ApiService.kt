package com.example.pelayananbantuangizi.data.api

import com.example.pelayananbantuangizi.data.api.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("lansia")
    suspend fun getLansia(
        @Query("nama") nama: String? = null,
        @Query("rw") rw: String? = null,
        @Query("kondisi_kesehatan") kondisiKesehatan: String? = null,
        @Query("status_bantuan") statusBantuan: String? = null,
        @Query("page") page: Int = 1
    ): Response<PagedResponse<LansiaDto>>

    @GET("lansia/{id}")
    suspend fun getLansiaDetail(@Path("id") id: Int): Response<DataWrapper<LansiaDto>>

    @POST("lansia")
    suspend fun createLansia(@Body request: CreateLansiaRequest): Response<DataWrapper<LansiaDto>>

    @PUT("lansia/{id}")
    suspend fun updateLansia(
        @Path("id") id: Int,
        @Body request: UpdateLansiaRequest
    ): Response<DataWrapper<LansiaDto>>

    @DELETE("lansia/{id}")
    suspend fun deleteLansia(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("lansia/{id}/foto-ktp")
    suspend fun uploadFotoKtp(
        @Path("id") id: Int,
        @Part foto: MultipartBody.Part
    ): Response<DataWrapper<LansiaDto>>

    @GET("lansia/{id}/status-bantuan")
    suspend fun getStatusBantuan(@Path("id") id: Int): Response<DataWrapper<StatusBantuanDto>>

    @GET("lansia/{id}/pemeriksaan")
    suspend fun getPemeriksaan(@Path("id") id: Int): Response<DataWrapper<List<PemeriksaanDto>>>

    @POST("lansia/{id}/pemeriksaan")
    suspend fun createPemeriksaan(
        @Path("id") id: Int,
        @Body request: CreatePemeriksaanRequest
    ): Response<DataWrapper<PemeriksaanDto>>
}
