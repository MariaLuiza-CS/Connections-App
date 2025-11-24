package com.picpay.desafio.android.di.feature

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.model.ContactUser
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.usecase.GetLocalCurrentUseCase
import com.picpay.desafio.android.presentation.main.MainEffect
import com.picpay.desafio.android.presentation.main.MainEvent
import com.picpay.desafio.android.presentation.main.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainModuleInstrumentedTest {
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        val fakeRepository = object : UserRepository {
            override fun getLocalCurrentUser(): Flow<com.picpay.desafio.android.domain.model.Result<ContactUser?>> =
                flow {
                    emit(
                        com.picpay.desafio.android.domain.model.Result.Success(
                            ContactUser(
                                "1",
                                "Alice",
                                "alice01",
                                "img.jpg"
                            )
                        )
                    )
                }

            override fun getCurrentUser(): Flow<Result<FirebaseUser?>> {
                TODO("Not yet implemented")
            }

            override fun signOut() {
                TODO("Not yet implemented")
            }

            override fun signInWithGoogle(idToken: String): Flow<Result<FirebaseUser>> {
                TODO("Not yet implemented")
            }

        }

        val useCase = GetLocalCurrentUseCase(fakeRepository)
        viewModel = MainViewModel(useCase, SavedStateHandle())
    }

    @Test
    fun getLocalCurrentUser_updatesUiStateAndSendsNavigateToHomeEffect() = runBlocking {
        viewModel.onEvent(MainEvent.GetLocalCurrentUser)
        delay(100)

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("Alice", state.currentLocalUser?.name)

        var effectReceived: MainEffect? = null
        viewModel.effect.collect { effect ->
            effectReceived = effect
            return@collect
        }

        assertEquals(MainEffect.NavigateToHome, effectReceived)
    }
}
