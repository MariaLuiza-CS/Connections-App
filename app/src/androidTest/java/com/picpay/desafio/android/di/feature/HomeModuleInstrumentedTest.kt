package com.picpay.desafio.android.di.feature

import com.picpay.desafio.android.data.repository.PeopleRepository
import com.picpay.desafio.android.domain.model.PersonWithPhotos
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.usecase.GetPeopleWithPhotosUseCase
import com.picpay.desafio.android.presentation.home.HomeEvent
import com.picpay.desafio.android.presentation.home.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeModuleInstrumentedTest {

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        val fakeRepository = object : PeopleRepository {
            override fun getPeople(): Flow<Result<List<PersonWithPhotos?>>> = flow {
                val fakePeople: List<PersonWithPhotos?> = listOf(
                    PersonWithPhotos(id = "1", fistName = "Alice", lastName = "Smith", photos = emptyList())
                )
                emit(Result.Success(fakePeople))
            }

        }

        val useCase = GetPeopleWithPhotosUseCase(fakeRepository)
        viewModel = HomeViewModel(useCase)
    }

    @Test
    fun loadPeopleUpdatesUIstate() = runBlocking {

        val initialState = viewModel.uiState.value
        assertEquals(1, initialState.peopleList.size)
        assertEquals("Alice", initialState.peopleList[0]?.fistName)


        viewModel.onEvent(HomeEvent.LoadPeopleList)
        delay(100)

        val updatedState = viewModel.uiState.value
        assertEquals(1, updatedState.peopleList.size)
        assertEquals("Alice", updatedState.peopleList[0]?.fistName)
    }
}
