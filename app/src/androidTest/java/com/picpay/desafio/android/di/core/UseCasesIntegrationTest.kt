package com.picpay.desafio.android.di.core

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.remote.network.createRetrofit
import com.picpay.desafio.android.data.remote.service.PersonService
import com.picpay.desafio.android.data.remote.service.PhotosService
import com.picpay.desafio.android.data.remote.service.PicPayService
import com.picpay.desafio.android.data.repository.ContactUserRepository
import com.picpay.desafio.android.data.repository.PeopleRepository
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.repository.ContactUserRepositoryImpl
import com.picpay.desafio.android.domain.repository.PeopleRepositoryImpl
import com.picpay.desafio.android.domain.repository.UserRepositoryImpl
import com.picpay.desafio.android.domain.usecase.GetContactUsersUseCase
import com.picpay.desafio.android.domain.usecase.GetLocalCurrentUseCase
import com.picpay.desafio.android.domain.usecase.GetPeopleWithPhotosUseCase
import com.picpay.desafio.android.domain.usecase.SignInWithGoogleUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UseCasesIntegrationTest {

    private lateinit var mockPicPayServer: MockWebServer
    private lateinit var mockPersonServer: MockWebServer
    private lateinit var mockPhotosServer: MockWebServer

    private lateinit var database: ConnectionsAppDataBase

    private lateinit var contactUserRepository: ContactUserRepository
    private lateinit var userRepository: UserRepository
    private lateinit var peopleRepository: PeopleRepository

    private lateinit var getContactUsersUseCase: GetContactUsersUseCase
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase
    private lateinit var getLocalCurrentUseCase: GetLocalCurrentUseCase
    private lateinit var getPeopleWithPhotosUseCase: GetPeopleWithPhotosUseCase

    private val client = OkHttpClient.Builder().build()

    @Before
    fun setup() {
        mockPicPayServer = MockWebServer()
        mockPersonServer = MockWebServer()
        mockPhotosServer = MockWebServer()

        mockPicPayServer.start()
        mockPersonServer.start()
        mockPhotosServer.start()


        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConnectionsAppDataBase::class.java
        ).allowMainThreadQueries().build()

        val picPayService = createRetrofit(mockPicPayServer.url("/").toString(), client)
            .create(PicPayService::class.java)

        val personService = createRetrofit(mockPersonServer.url("/").toString(), client)
            .create(PersonService::class.java)

        val photosService = createRetrofit(mockPhotosServer.url("/").toString(), client)
            .create(PhotosService::class.java)

        contactUserRepository = ContactUserRepositoryImpl(picPayService, database.contactUserDao())
        userRepository = UserRepositoryImpl(FirebaseAuth.getInstance(), database.userDao())
        peopleRepository = PeopleRepositoryImpl(personService, photosService, database.peopleDao())


        getContactUsersUseCase = GetContactUsersUseCase(contactUserRepository)
        signInWithGoogleUseCase = SignInWithGoogleUseCase(userRepository)
        getLocalCurrentUseCase = GetLocalCurrentUseCase(userRepository)
        getPeopleWithPhotosUseCase = GetPeopleWithPhotosUseCase(peopleRepository)
    }

    @After
    fun tearDown() {
        mockPicPayServer.shutdown()
        mockPersonServer.shutdown()
        mockPhotosServer.shutdown()
        database.close()
    }

    @Test
    fun getContactUsersUseCaseReturnsDatafromAPI() = runBlocking {
        val jsonResponse = """
            [
                {"id":"1","name":"Alice","username":"alice01","img":"img1.jpg"},
                {"id":"2","name":"Bob","username":"bob02","img":"img2.jpg"}
            ]
        """.trimIndent()
        mockPicPayServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val result = getContactUsersUseCase().first()
        assertTrue(result is Result.Success)
        assertEquals(2, (result as Result.Success).data.size)
    }

    @Test
    fun getLocalCurrentUseCaseReturnsCurrentUserDromDatabase() = runBlocking {

        val user = com.picpay.desafio.android.data.local.entity.UserEntity(
            id = "uid123",
            name = "Test User",
            email = "test@example.com",
            img = "img.jpg"
        )
        database.userDao().insertUser(user)

        val result = getLocalCurrentUseCase().first()
        assertTrue(result is Result.Success)
        assertEquals("uid123", (result as Result.Success).data?.id)
    }

    @Test
    fun getPeopleWithPhotosUseCaseReturnsPeopleWithPhotos() = runBlocking {
        val personJson = """
            {
              "results": [
                {
                  "gender": "female",
                  "name": {"title": "Ms", "first": "Ada", "last": "Lovelace"},
                  "email": "ada@example.com",
                  "picture": {"large": "url_image"},
                  "login": {"uuid": "uuid-123"}
                }
              ]
            }
        """.trimIndent()

        mockPersonServer.enqueue(MockResponse().setBody(personJson).setResponseCode(200))
        mockPhotosServer.enqueue(
            MockResponse().setBody(
                """
            [
                {"download_url": "url_photo1"}
            ]
        """.trimIndent()
            ).setResponseCode(200)
        )

        val result = getPeopleWithPhotosUseCase().first()
        assertTrue(result is Result.Success)
        val people = (result as Result.Success).data
        assertEquals(1, people.size)
        assertEquals("Ada", people.first()?.fistName)
    }
}