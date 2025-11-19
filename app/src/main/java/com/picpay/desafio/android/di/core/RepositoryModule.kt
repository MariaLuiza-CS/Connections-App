package com.picpay.desafio.android.di.core

import com.picpay.desafio.android.data.repository.AuthenticationRepository
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.repository.AuthenticationRepositoryImpl
import com.picpay.desafio.android.domain.repository.UserRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> {
        UserRepositoryImpl(
            picPayService = get(),
            userDao = get()
        )
    }
    single<AuthenticationRepository> {
        AuthenticationRepositoryImpl(
            firebaseAuth = get(),
            authenticationDao = get()
        )
    }
}
