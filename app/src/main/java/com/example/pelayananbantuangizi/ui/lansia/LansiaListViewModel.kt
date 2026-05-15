package com.example.pelayananbantuangizi.ui.lansia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pelayananbantuangizi.data.api.model.LansiaDto
import com.example.pelayananbantuangizi.data.api.model.MetaDto
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LansiaListState(
    val items: List<LansiaDto> = emptyList(),
    val meta: MetaDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class LansiaListViewModel(private val repository: LansiaRepository) : ViewModel() {

    private val _state = MutableStateFlow(LansiaListState())
    val state: StateFlow<LansiaListState> = _state.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    var searchNama: String = ""
        private set
    var filterRw: String = ""
        private set
    var filterKondisi: String = ""
        private set
    var filterStatus: String = ""
        private set
    private var currentPage: Int = 1

    init {
        loadLansia()
    }

    fun loadLansia(page: Int = 1) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            currentPage = page
            val result = repository.getLansia(
                nama = searchNama.ifBlank { null },
                rw = filterRw.ifBlank { null },
                kondisiKesehatan = filterKondisi.ifBlank { null },
                statusBantuan = filterStatus.ifBlank { null },
                page = page
            )
            result.fold(
                onSuccess = { response ->
                    _state.value = LansiaListState(
                        items = response.data,
                        meta = response.meta,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _state.value = _state.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    fun search(nama: String) {
        searchNama = nama
        loadLansia()
    }

    fun applyFilter(rw: String = "", kondisi: String = "", status: String = "") {
        filterRw = rw
        filterKondisi = kondisi
        filterStatus = status
        loadLansia()
    }

    fun clearFilters() {
        searchNama = ""
        filterRw = ""
        filterKondisi = ""
        filterStatus = ""
        loadLansia()
    }

    fun nextPage() {
        val meta = _state.value.meta ?: return
        if (currentPage < meta.lastPage) loadLansia(currentPage + 1)
    }

    fun prevPage() {
        if (currentPage > 1) loadLansia(currentPage - 1)
    }

    fun deleteLansia(id: Int) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            val result = repository.deleteLansia(id)
            result.fold(
                onSuccess = {
                    _deleteState.value = UiState.Success(Unit)
                    loadLansia(currentPage)
                },
                onFailure = { _deleteState.value = UiState.Error(it.message ?: "Gagal hapus") }
            )
        }
    }

    fun resetDeleteState() {
        _deleteState.value = UiState.Idle
    }
}
