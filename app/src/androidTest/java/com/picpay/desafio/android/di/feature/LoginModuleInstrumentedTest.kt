package com.picpay.desafio.android.di.feature

import android.net.Uri
import android.os.Parcel
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseUserMetadata
import com.google.firebase.auth.MultiFactor
import com.google.firebase.auth.MultiFactorInfo
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.zzan
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.model.ContactUser
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.usecase.SignInWithGoogleUseCase
import com.picpay.desafio.android.presentation.login.LoginEvent
import com.picpay.desafio.android.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LoginModuleInstrumentedTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var fakeUseCase: SignInWithGoogleUseCase

    @Before
    fun setup() {
        val mockUser: FirebaseUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn("123")
        `when`(mockUser.email).thenReturn("test@example.com")

        val fakeRepository = object : UserRepository {
            override fun getLocalCurrentUser() = flow {
                emit(Result.Success(
                    ContactUser("uid123", "Test User", "testuser", "img.jpg")
                ))
            }
            override fun getCurrentUser() = flow {
                emit(Result.Success(mockUser))
            }
            override fun signOut() {}
            override fun signInWithGoogle(idToken: String) = flow {
                emit(Result.Success(mockUser))
            }
        }

        fakeUseCase = SignInWithGoogleUseCase(fakeRepository)
        viewModel = LoginViewModel(fakeUseCase, SavedStateHandle())
    }

    @Test
    fun loginWithValidTokenUpdatesUiStateToSuccess () = runTest {
        viewModel.onEvent(LoginEvent.SignInGoogle("valid_token"))

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun loginWithInvalidTokenUpdatesUiStateToError () = runTest {
        viewModel.onEvent(LoginEvent.SignInGoogle("invalid_token"))

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("Login failed", state.error)
    }
}