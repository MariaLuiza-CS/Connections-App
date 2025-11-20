package com.picpay.desafio.android.presentation.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.usecase.GetLocalCurrentUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(
    private val getLocalCurrentUseCase: GetLocalCurrentUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        savedStateHandle.get<MainUiState>("mainUiState") ?: MainUiState()
    )
    val uiState: StateFlow<MainUiState> = _uiState

    private val _effect = MutableSharedFlow<MainEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            _uiState.collect { newState ->
                savedStateHandle["mainUiState"] = newState
            }
        }
    }

    fun onEvent(mainEvent: MainEvent) {
        when (mainEvent) {
            is MainEvent.getLocalCurrentUser -> getLocalCurrentUser()
        }
    }

    private suspend fun sendEffect(effect: MainEffect) {
        _effect.emit(effect)
    }

    private fun getLocalCurrentUser() {
        viewModelScope.launch {
            getLocalCurrentUseCase().collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data != null) {
                            sendEffect(
                                MainEffect.NavigateToHome
                            )
                        } else {
                            sendEffect(
                                MainEffect.NavigateToLogin
                            )
                        }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentLocalUser = result.data
                        )
                    }

                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                        sendEffect(
                            MainEffect.NavigateToLogin
                        )
                    }
                }
            }
        }
    }
}