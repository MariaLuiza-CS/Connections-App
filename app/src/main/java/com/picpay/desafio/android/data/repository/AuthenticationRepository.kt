package com.picpay.desafio.android.data.repository

import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.local.AuthenticationPersonEntity
import com.picpay.desafio.android.domain.model.AuthenticationUser
import com.picpay.desafio.android.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun getLocalCurrentUser(): Flow<Result<AuthenticationUser?>>
    fun getCurrentUser(): Flow<Result<FirebaseUser?>>
    fun signOut()
    fun signInWithGoogle(idToken: String): Flow<Result<FirebaseUser>>
}
