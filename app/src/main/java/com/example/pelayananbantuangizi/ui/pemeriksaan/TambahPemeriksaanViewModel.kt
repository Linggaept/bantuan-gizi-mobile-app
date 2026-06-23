package com.example.pelayananbantuangizi.ui.pemeriksaan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pelayananbantuangizi.data.api.model.CreatePemeriksaanRequest
import com.example.pelayananbantuangizi.data.api.model.PemeriksaanDto
import com.example.pelayananbantuangizi.data.repository.PemeriksaanRepository
import com.example.pelayananbantuangizi.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TambahPemeriksaanViewModel(private val repository: PemeriksaanRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<PemeriksaanDto>>(UiState.Idle)
    val state: StateFlow<UiState<PemeriksaanDto>> = _state.asStateFlow()

    fun submit(
        lansiaId: Int,
        beratBadan: String,
        tekananDarah: String,
        catatan: String
    ) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = repository.createPemeriksaan(
                lansiaId,
                CreatePemeriksaanRequest(
                    beratBadan = beratBadan.toDoubleOrNull(),
                    tekananDarah = tekananDarah.ifBlank { null },
                    catatan = catatan.ifBlank { null }
                )
            )
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Gagal menyimpan") }
            )
        }
    }

    fun reset() { _state.value = UiState.Idle }
}
