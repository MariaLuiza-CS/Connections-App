package com.picpay.desafio.android.di.feature

import androidx.lifecycle.SavedStateHandle
import com.picpay.desafio.android.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

var mainModule = module {
    viewModel { (savedStateHandle: SavedStateHandle) ->
        MainViewModel(
            getLocalCurrentUseCase = get(),
            savedStateHandle = savedStateHandle
        )
    }
}
