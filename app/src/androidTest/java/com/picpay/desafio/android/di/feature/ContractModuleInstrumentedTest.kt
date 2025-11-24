package com.picpay.desafio.android.di.feature

import androidx.lifecycle.SavedStateHandle
import com.picpay.desafio.android.data.repository.ContactUserRepository
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.usecase.GetContactUsersUseCase
import com.picpay.desafio.android.presentation.contact.ContactEvent
import com.picpay.desafio.android.presentation.contact.ContactViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ContractModuleInstrumentedTest {
    private lateinit var viewModel: ContactViewModel

    @Before
    fun setup() {
        val fakeRepository = object : ContactUserRepository {
            override fun getContactUsers(): Flow<com.picpay.desafio.android.domain.model.Result<List<User>>> =
                flow {
                    emit(Result.Success(listOf(User("1", "Alice", "alice"))))
                }
        }

        val useCase = GetContactUsersUseCase(fakeRepository)
        viewModel = ContactViewModel(useCase, SavedStateHandle())
    }

    @Test
    fun loadUsersUpdatesUiState() = runBlocking {
        viewModel.onEvent(ContactEvent.LoadUsersList)
        delay(100)
        val state = viewModel.uiState.value

        assertEquals(1, state.users.size)
        assertEquals("Alice", state.users.first().name)
    }
}