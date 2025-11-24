package com.picpay.desafio.android.di.feature

import com.picpay.desafio.android.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModel(
            getPeopleWithPhotosUseCase = get()
        )
    }
}
