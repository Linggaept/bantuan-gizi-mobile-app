package com.example.pelayananbantuangizi.data.api.model

import com.google.gson.annotations.SerializedName

// --- Wrappers ---
data class DataWrapper<T>(val data: T)

data class PagedResponse<T>(
    val data: List<T>,
    val links: LinksDto,
    val meta: MetaDto
)

data class LinksDto(
    val first: String?,
    val last: String?,
    val prev: String?,
    val next: String?
)

data class MetaDto(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Int
)

// --- Auth ---
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(val data: LoginData)

data class LoginData(val token: String, val user: UserDto)

data class UserDto(val id: Int, val nama: String, val role: String)

// --- Lansia ---
data class LansiaDto(
    @SerializedName("lansia_id") val lansiaId: Int,
    val nik: String,
    val nama: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String,
    val usia: Int,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    val alamat: String,
    val rt: String?,
    val rw: String,
    @SerializedName("foto_ktp") val fotoKtp: String?,
    @SerializedName("kondisi_kesehatan") val kondisiKesehatan: String?,
    @SerializedName("created_by") val createdBy: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class CreateLansiaRequest(
    val nik: String,
    val nama: String,
    @SerializedName("tanggal_lahir") val tanggalLahir: String,
    @SerializedName("jenis_kelamin") val jenisKelamin: String,
    val alamat: String,
    val rt: String?,
    val rw: String
)

data class UpdateLansiaRequest(
    val nik: String?,
    val nama: String?,
    @SerializedName("tanggal_lahir") val tanggalLahir: String?,
    @SerializedName("jenis_kelamin") val jenisKelamin: String?,
    val alamat: String?,
    val rt: String?,
    val rw: String?
)

// --- Status Bantuan ---
data class StatusBantuanDto(
    @SerializedName("bantuan_id") val bantuanId: Int?,
    @SerializedName("lansia_id") val lansiaId: Int,
    @SerializedName("periode_bulan") val periodeBulan: Int?,
    @SerializedName("periode_tahun") val periodeTahun: Int?,
    @SerializedName("skor_ranking") val skorRanking: Double?,
    @SerializedName("status_penerima") val statusPenerima: String?,
    @SerializedName("approved_by") val approvedBy: Int?,
    @SerializedName("approved_at") val approvedAt: String?
)

// --- Pemeriksaan ---
data class PemeriksaanDto(
    @SerializedName("pemeriksaan_id") val pemeriksaanId: Int,
    @SerializedName("lansia_id") val lansiaId: Int,
    @SerializedName("tanggal_periksa") val tanggalPeriksa: String,
    @SerializedName("berat_badan") val beratBadan: Double?,
    @SerializedName("tekanan_darah") val tekananDarah: String?,
    @SerializedName("hasil_periksa") val hasilPeriksa: String,
    val catatan: String?
)

data class CreatePemeriksaanRequest(
    @SerializedName("tanggal_periksa") val tanggalPeriksa: String,
    @SerializedName("berat_badan") val beratBadan: Double?,
    @SerializedName("tekanan_darah") val tekananDarah: String?,
    @SerializedName("hasil_periksa") val hasilPeriksa: String,
    val catatan: String?
)
