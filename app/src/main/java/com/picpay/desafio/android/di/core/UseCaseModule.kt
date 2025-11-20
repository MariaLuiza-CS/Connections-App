package com.picpay.desafio.android.di.core

import com.picpay.desafio.android.domain.usecase.GetLocalCurrentUseCase
import com.picpay.desafio.android.domain.usecase.GetUsersUseCase
import com.picpay.desafio.android.domain.usecase.SignInWithGoogleUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory {
        GetUsersUseCase(
            userRepository = get()
        )
    }
    factory {
        SignInWithGoogleUseCase(
            authenticationRepository = get()
        )
    }
    factory {
        GetLocalCurrentUseCase(
            authenticationRepository = get()
        )
    }
}
