package com.picpay.desafio.android.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.picpay.desafio.android.data.local.AuthenticationDao
import com.picpay.desafio.android.data.repository.AuthenticationRepository
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.util.toAuthenticationPersonEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val authenticationDao: AuthenticationDao
) : AuthenticationRepository {
    override fun getCurrentUser(): Flow<Result<FirebaseUser?>> = flow {
        emit(Result.Loading)

        try {
            val user = firebaseAuth.currentUser
            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(
                Result.Error(
                    exception = e,
                    message = e.message
                )
            )
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun signInWithGoogle(idToken: String): Flow<Result<FirebaseUser>> = flow {
        emit(Result.Loading)
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user!!
            emit(
                Result.Success(firebaseUser)
            )
            authenticationDao.insertAuthenticationPerson(
                firebaseUser.toAuthenticationPersonEntity()
            )
        } catch (e: Exception) {
            emit(
                Result.Error(
                    exception = e,
                    message = e.message
                )
            )
        }
    }
}
