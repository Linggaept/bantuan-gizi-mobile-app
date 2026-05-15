package com.example.pelayananbantuangizi.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pelayananbantuangizi.data.api.model.UserDto
import com.example.pelayananbantuangizi.data.repository.AuthRepository
import com.example.pelayananbantuangizi.util.ApiException
import com.example.pelayananbantuangizi.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UserDto>>(UiState.Idle)
    val uiState: StateFlow<UiState<UserDto>> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = UiState.Error("Email dan password wajib diisi")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = authRepository.login(email, password)
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { e ->
                    val msg = if (e is ApiException && e.code == 422) {
                        "Email atau password salah"
                    } else {
                        e.message ?: "Login gagal"
                    }
                    UiState.Error(msg)
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
    }
}
