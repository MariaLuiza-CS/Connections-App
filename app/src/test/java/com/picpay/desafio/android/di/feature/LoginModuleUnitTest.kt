package com.picpay.desafio.android.di.feature

import androidx.lifecycle.SavedStateHandle
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.usecase.SignInWithGoogleUseCase
import com.picpay.desafio.android.presentation.login.LoginEvent
import com.picpay.desafio.android.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class LoginModuleUnitTest {
    private lateinit var viewModel: LoginViewModel
    private val fakeUseCase: SignInWithGoogleUseCase = mock()

    @Before
    fun setup() {
        viewModel = LoginViewModel(
            signInWithGoogleUseCase = fakeUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }

    @Test
    fun `sign in google success updates UI state`() = runBlocking {
        val fakeUser = mock<com.google.firebase.auth.FirebaseUser>()
        whenever(fakeUseCase("fake_id_token")).thenReturn(flow { emit(Result.Success(fakeUser)) })

        viewModel.onEvent(LoginEvent.SignInGoogle("fake_id_token"))

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
    }
}
