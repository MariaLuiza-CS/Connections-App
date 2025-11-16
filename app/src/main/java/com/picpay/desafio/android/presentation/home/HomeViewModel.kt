package com.picpay.desafio.android.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        savedStateHandle.get<HomeUiState>("homeUiState") ?: HomeUiState()
    )
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            _uiState.collect { newState ->
                savedStateHandle["homeUiState"] = newState
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadUsers -> loadUsers()
        }
    }

    private suspend fun sendEffect(effect: HomeEffect) {
        _effect.emit(effect)
    }

    private fun loadUsers() {
        viewModelScope.launch {
            getUsersUseCase().collectLatest { result ->
                when (result) {
                    is com.picpay.desafio.android.domain.model.Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is com.picpay.desafio.android.domain.model.Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null,
                            users = result.data
                        )
                    }

                    is com.picpay.desafio.android.domain.model.Result.Error -> {
                        val hasLocalUser = _uiState.value.users.isNotEmpty()

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = if (hasLocalUser) null else (result.message ?: "Unknown error")
                        )

                        if (!hasLocalUser) {
                            sendEffect(
                                HomeEffect.NavigateToError(
                                    result.message ?: "Unknown error"
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
