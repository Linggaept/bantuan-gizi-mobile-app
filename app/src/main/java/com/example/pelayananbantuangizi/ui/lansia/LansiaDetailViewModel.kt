package com.example.pelayananbantuangizi.ui.lansia

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pelayananbantuangizi.data.api.model.LansiaDto
import com.example.pelayananbantuangizi.data.api.model.StatusBantuanDto
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class LansiaDetailViewModel(private val repository: LansiaRepository) : ViewModel() {

    private val _lansiaState = MutableStateFlow<UiState<LansiaDto>>(UiState.Loading)
    val lansiaState: StateFlow<UiState<LansiaDto>> = _lansiaState.asStateFlow()

    private val _statusBantuanState = MutableStateFlow<UiState<StatusBantuanDto>>(UiState.Idle)
    val statusBantuanState: StateFlow<UiState<StatusBantuanDto>> = _statusBantuanState.asStateFlow()

    private val _uploadState = MutableStateFlow<UiState<LansiaDto>>(UiState.Idle)
    val uploadState: StateFlow<UiState<LansiaDto>> = _uploadState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch {
            _lansiaState.value = UiState.Loading
            val result = repository.getLansiaDetail(id)
            _lansiaState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Gagal memuat data") }
            )
        }
        viewModelScope.launch {
            _statusBantuanState.value = UiState.Loading
            val result = repository.getStatusBantuan(id)
            _statusBantuanState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Idle } // non-critical
            )
        }
    }

    fun uploadFoto(id: Int, uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            _uploadState.value = UiState.Loading
            try {
                val bytes = contentResolver.openInputStream(uri)?.readBytes()
                    ?: run {
                        _uploadState.value = UiState.Error("Gagal membaca file")
                        return@launch
                    }
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("foto_ktp", "foto_ktp.jpg", requestBody)
                val result = repository.uploadFotoKtp(id, part)
                _uploadState.value = result.fold(
                    onSuccess = {
                        _lansiaState.value = UiState.Success(it)
                        UiState.Success(it)
                    },
                    onFailure = { UiState.Error(it.message ?: "Upload gagal") }
                )
            } catch (e: Exception) {
                _uploadState.value = UiState.Error(e.message ?: "Upload gagal")
            }
        }
    }

    fun deleteLansia(id: Int) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = repository.deleteLansia(id)
            _deleteState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Gagal menghapus") }
            )
        }
    }

    fun resetUploadState() { _uploadState.value = UiState.Idle }
    fun resetDeleteState() { _deleteState.value = UiState.Idle }
}
