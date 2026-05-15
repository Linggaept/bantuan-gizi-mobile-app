package com.example.pelayananbantuangizi.ui.lansia

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pelayananbantuangizi.data.api.model.CreateLansiaRequest
import com.example.pelayananbantuangizi.data.api.model.LansiaDto
import com.example.pelayananbantuangizi.data.api.model.UpdateLansiaRequest
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class LansiaFormViewModel(private val repository: LansiaRepository) : ViewModel() {

    private val _submitState = MutableStateFlow<UiState<LansiaDto>>(UiState.Idle)
    val submitState: StateFlow<UiState<LansiaDto>> = _submitState.asStateFlow()

    private val _loadState = MutableStateFlow<UiState<LansiaDto>>(UiState.Idle)
    val loadState: StateFlow<UiState<LansiaDto>> = _loadState.asStateFlow()

    fun loadForEdit(id: Int) {
        viewModelScope.launch {
            _loadState.value = UiState.Loading
            val result = repository.getLansiaDetail(id)
            _loadState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Gagal memuat data") }
            )
        }
    }

    fun submit(
        lansiaId: Int?,
        nik: String,
        nama: String,
        tanggalLahir: String,
        jenisKelamin: String,
        alamat: String,
        rt: String,
        rw: String,
        fotoUri: Uri?,
        contentResolver: ContentResolver
    ) {
        val errors = mutableListOf<String>()
        if (nik.length != 16) errors.add("NIK harus 16 karakter")
        if (nama.isBlank()) errors.add("Nama wajib diisi")
        if (tanggalLahir.isBlank()) errors.add("Tanggal lahir wajib diisi")
        if (jenisKelamin !in listOf("L", "P")) errors.add("Jenis kelamin wajib dipilih")
        if (alamat.isBlank()) errors.add("Alamat wajib diisi")
        if (rw.isBlank()) errors.add("RW wajib diisi")

        if (errors.isNotEmpty()) {
            _submitState.value = UiState.Error(errors.joinToString("\n"))
            return
        }

        viewModelScope.launch {
            _submitState.value = UiState.Loading

            val result = if (lansiaId == null) {
                repository.createLansia(
                    CreateLansiaRequest(
                        nik = nik,
                        nama = nama,
                        tanggalLahir = tanggalLahir,
                        jenisKelamin = jenisKelamin,
                        alamat = alamat,
                        rt = rt.ifBlank { null },
                        rw = rw
                    )
                )
            } else {
                repository.updateLansia(
                    lansiaId,
                    UpdateLansiaRequest(
                        nik = nik,
                        nama = nama,
                        tanggalLahir = tanggalLahir,
                        jenisKelamin = jenisKelamin,
                        alamat = alamat,
                        rt = rt.ifBlank { null },
                        rw = rw
                    )
                )
            }

            result.fold(
                onSuccess = { lansia ->
                    // Upload foto if selected
                    if (fotoUri != null) {
                        val uploadResult = uploadFoto(lansia.lansiaId, fotoUri, contentResolver)
                        uploadResult.fold(
                            onSuccess = { _submitState.value = UiState.Success(it) },
                            onFailure = {
                                // Lansia saved but foto failed — still success with concern
                                _submitState.value = UiState.Success(lansia)
                            }
                        )
                    } else {
                        _submitState.value = UiState.Success(lansia)
                    }
                },
                onFailure = {
                    _submitState.value = UiState.Error(it.message ?: "Gagal menyimpan data")
                }
            )
        }
    }

    private suspend fun uploadFoto(id: Int, uri: Uri, contentResolver: ContentResolver): Result<LansiaDto> {
        return try {
            val bytes = contentResolver.openInputStream(uri)?.readBytes()
                ?: return Result.failure(Exception("Gagal membaca file"))
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("foto_ktp", "foto_ktp.jpg", requestBody)
            repository.uploadFotoKtp(id, part)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetSubmitState() {
        _submitState.value = UiState.Idle
    }
}
