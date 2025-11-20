package com.picpay.desafio.android.domain.usecase

import com.picpay.desafio.android.data.repository.AuthenticationRepository
import com.picpay.desafio.android.domain.model.AuthenticationUser
import com.picpay.desafio.android.domain.model.Result
import kotlinx.coroutines.flow.Flow

class GetLocalCurrentUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    operator fun invoke(): Flow<Result<AuthenticationUser?>> {
        return authenticationRepository.getLocalCurrentUser()
    }
}
