package com.picpay.desafio.android.domain

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.dao.ContactUserDao
import com.picpay.desafio.android.data.local.entity.ContactUserEntity
import com.picpay.desafio.android.data.remote.dto.ContactUserResponseDto
import com.picpay.desafio.android.data.remote.service.PicPayService
import com.picpay.desafio.android.data.repository.ContactUserRepository
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.repository.ContactUserRepositoryImpl
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeContactUserDao : ContactUserDao {
    private val data = MutableStateFlow<List<ContactUserEntity>>(emptyList())

    override suspend fun insertContactUsersList(contactUsersList: List<ContactUserEntity>) {
        data
    }

    override fun getContactUsersList(): Flow<List<ContactUserEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun cleanContactUsersList() {
        data.value = emptyList()
    }

}

class FakePicPayService : PicPayService {

    var error: Exception? = null
    var returnedList: List<ContactUserResponseDto> = emptyList()
    var calls = 0

    override suspend fun getContactUsers(): List<ContactUserResponseDto> {
        calls++
        error?.let { throw it }
        return returnedList
    }
}

class ContactUsersRepositoryImplInstrumentTest {

    private lateinit var database: ConnectionsAppDataBase
    private lateinit var dao: ContactUserDao
    private lateinit var service: FakePicPayService
    private lateinit var repository: ContactUserRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            ConnectionsAppDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.contactUserDao()
        service = FakePicPayService()

        repository = ContactUserRepositoryImpl(
            picPayService = service,
            contactUserDao = dao
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun whenDaoHasData_emitsLoadingThenSuccessWithLocal() = runBlocking {

        dao.insertContactUsersList(
            listOf(
                ContactUserEntity(
                    id = "1",
                    name = "João",
                    username = "j",
                    img = "x"
                )
            )
        )

        service.error = RuntimeException("API fail")

        val emissions = mutableListOf<Result<List<User>>>()

        val job = launch {
            repository.getContactUsers().collect {
                emissions.add(it)
                if (emissions.size >= 2) cancel()
            }
        }

        job.join()

        assertTrue(emissions[0] is Result.Loading)

        val success = emissions[1] as Result.Success
        assertEquals("João", success.data.first().name)
    }

    @Test
    fun whenDaoEmpty_andApiFails_emitsLoadingThenError() = runBlocking {

        service.error = RuntimeException("fail")

        val emissions = mutableListOf<Result<List<User>>>()

        val job = launch {
            repository.getContactUsers().collect {
                emissions.add(it)
                if (emissions.size >= 2) cancel()
            }
        }

        job.join()

        assertTrue(emissions[0] is Result.Loading)

        val error = emissions[1] as Result.Error
        assertEquals("fail", error.message)
    }

    @Test
    fun whenDaoEmpty_andApiSuccess_savesToDbAndEmitsSuccess() = runBlocking {

        service.returnedList = listOf(
            ContactUserResponseDto(
                id = "100",
                name = "Ana",
                username = "ana",
                img = "img"
            )
        )

        val emissions = mutableListOf<Result<List<User>>>()

        val job = launch {
            repository.getContactUsers().collect {
                emissions.add(it)
                if (emissions.size >= 2) cancel()
            }
        }

        job.join()

        assertTrue(emissions[0] is Result.Loading)

        val success = emissions[1] as Result.Success
        assertEquals("Ana", success.data.first().name)

        val saved = dao.getContactUsersList().first()
        assertEquals(1, saved.size)
        assertEquals("Ana", saved.first().name)
    }

    @Test
    fun whenDaoChangesFlowEmitsAgain() = runBlocking {

        val emissions = mutableListOf<Result<List<User>>>()

        val job = launch {
            repository.getContactUsers().collect {
                emissions.add(it)
                if (emissions.size >= 3) cancel()
            }
        }

        delay(200)

        dao.insertContactUsersList(
            listOf(
                ContactUserEntity(
                    id = "10",
                    name = "Carlos",
                    username = "c",
                    img = "x"
                )
            )
        )

        job.join()

        val last = emissions.last() as Result.Success
        assertEquals("Carlos", last.data.first().name)
    }

    @Test
    fun whenCallingRepository_callsApiOneTime() = runBlocking {

        val fakeDao = FakeContactUserDao()
        val fakeService = FakePicPayService()

        val repo = ContactUserRepositoryImpl(
            fakeService,
            fakeDao
        )

        val job = launch {
            repo.getContactUsers().collect {
                cancel()
            }
        }

        job.join()

        assertEquals(1, fakeService.calls)
    }
}
