package com.picpay.desafio.android.di.feature

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.repository.ContactUserRepository
import com.picpay.desafio.android.data.repository.PeopleRepository
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.model.ContactUser
import com.picpay.desafio.android.domain.model.PersonWithPhotos
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.usecase.GetContactUsersUseCase
import com.picpay.desafio.android.domain.usecase.GetLocalCurrentUseCase
import com.picpay.desafio.android.domain.usecase.GetPeopleWithPhotosUseCase
import com.picpay.desafio.android.presentation.profile.ProfileEvent
import com.picpay.desafio.android.presentation.profile.ProfileViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ProfileModuleInstrumentedTest {
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        // Mock do FirebaseUser, caso necessário
        val mockUser: FirebaseUser = mock(FirebaseUser::class.java)
        `when`(mockUser.uid).thenReturn("123")
        `when`(mockUser.email).thenReturn("test@example.com")

        // Fake UserRepository
        val fakeUserRepository = object : UserRepository {
            override fun getLocalCurrentUser() = flow {
                emit(
                    Result.Success(
                        ContactUser("uid123", "Alice", "alice01", "img.jpg")
                    )
                )
            }

            override fun getCurrentUser() = flow { emit(Result.Success(mockUser)) }
            override fun signOut() {}
            override fun signInWithGoogle(idToken: String) = flow { emit(Result.Success(mockUser)) }
        }

        // Fake ContactUserRepository
        val fakeContactRepository = object : ContactUserRepository {
            override fun getContactUsers() = flow {
                emit(Result.Success(listOf(User("1", "Bob", "bob01", "img2.jpg"))))
            }
        }

        // Fake PeopleRepository
        val fakePeopleRepository = object : PeopleRepository {
            override fun getPeople() = flow {
                val peopleList: List<PersonWithPhotos?> = listOf(
                    PersonWithPhotos("1", "Charlie", "Brown", "", "", "", null, emptyList())
                )
                emit(Result.Success(peopleList))
            }
        }

        // Use Cases
        val getLocalCurrentUseCase = GetLocalCurrentUseCase(fakeUserRepository)
        val getContactUsersUseCase = GetContactUsersUseCase(fakeContactRepository)
        val getPeopleWithPhotosUseCase = GetPeopleWithPhotosUseCase(fakePeopleRepository)

        // ViewModel
        viewModel = ProfileViewModel(
            getGetLocalCurrentUseCase = getLocalCurrentUseCase,
            getContactUsersUseCase = getContactUsersUseCase,
            getPeopleWithPhotosUseCase = getPeopleWithPhotosUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }

    @Test
    fun loadCurrentUser_updatesUiState() = runTest {
        viewModel.onEvent(ProfileEvent.LoadCurrentUser)

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals("Alice", state.currentUser?.name)
    }

    @Test
    fun loadContactUserList_updatesUiState() = runTest {
        viewModel.onEvent(ProfileEvent.LoadContactUserList)

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.contactUsersList.size)
        assertEquals("Bob", state.contactUsersList.first()?.name)
    }

    @Test
    fun loadPeopleWithPhotoList_updatesUiState() = runTest {
        viewModel.onEvent(ProfileEvent.LoadPeopleWithPhotoList)

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(1, state.peopleWithPhotosList.size)
        assertEquals("Charlie", state.peopleWithPhotosList.first()?.fistName)
    }
}