package com.picpay.desafio.android.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.usecase.GetPeopleWithPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPeopleWithPhotosUseCase: GetPeopleWithPhotosUseCase
) : ViewModel() {

    companion object {
        private const val HOME_UI_STATE_KEY = "homeUiState"
    }

    private var _uiState = MutableStateFlow(HomeUIState())
    var uiState: StateFlow<HomeUIState> = _uiState


    init {
        loadPeopleList()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.loadPeopleList -> {
                loadPeopleList()
            }
        }
    }

    private fun loadPeopleList() {
        viewModelScope.launch {
            getPeopleWithPhotosUseCase().collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }

                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = null,
                            peopleList = result.data
                        )
                    }

                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message ?: "error to get people list"
                        )
                    }
                }
            }
        }
    }
}