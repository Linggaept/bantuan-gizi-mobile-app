package com.example.pelayananbantuangizi.ui.pemeriksaan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pelayananbantuangizi.data.api.model.PemeriksaanDto
import com.example.pelayananbantuangizi.data.repository.PemeriksaanRepository
import com.example.pelayananbantuangizi.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PemeriksaanListViewModel(private val repository: PemeriksaanRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<PemeriksaanDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<PemeriksaanDto>>> = _state.asStateFlow()

    fun load(lansiaId: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = repository.getPemeriksaan(lansiaId)
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Gagal memuat riwayat") }
            )
        }
    }
}
