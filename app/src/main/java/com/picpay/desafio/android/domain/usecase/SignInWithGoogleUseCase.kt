package com.picpay.desafio.android.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.repository.AuthenticationRepository
import com.picpay.desafio.android.domain.model.Result
import kotlinx.coroutines.flow.Flow

class SignInWithGoogleUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    operator fun invoke(idToken: String): Flow<Result<FirebaseUser>> {
        return authenticationRepository.signInWithGoogle(idToken)
    }
}