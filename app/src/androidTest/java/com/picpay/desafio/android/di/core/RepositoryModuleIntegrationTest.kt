package com.picpay.desafio.android.di.core

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.dao.ContactUserDao
import com.picpay.desafio.android.data.local.dao.PeopleDao
import com.picpay.desafio.android.data.local.dao.UserDao
import com.picpay.desafio.android.data.local.entity.UserEntity
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import retrofit2.Retrofit

class RepositoryModuleIntegrationTest {

    private lateinit var database: ConnectionsAppDataBase
    private lateinit var contactUserDao: ContactUserDao
    private lateinit var userDao: UserDao
    private lateinit var peopleDao: PeopleDao

    private lateinit var mockWebServerPicPay: MockWebServer
    private lateinit var mockWebServerPerson: MockWebServer
    private lateinit var mockWebServerPhotos: MockWebServer

    private lateinit var picPayService: PicPayService
    private lateinit var personService: PersonService
    private lateinit var photosService: PhotosService
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var contactUserRepository: ContactUserRepository
    private lateinit var userRepository: UserRepository
    private lateinit var peopleRepository: PeopleRepository

    object JsonConfig {
        val instance: Json = Json { ignoreUnknownKeys = true }
    }

    @Before
    fun setup() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConnectionsAppDataBase::class.java
        ).allowMainThreadQueries().build()

        contactUserDao = database.contactUserDao()
        userDao = database.userDao()
        peopleDao = database.peopleDao()

        mockWebServerPicPay = MockWebServer().apply { start() }
        mockWebServerPerson = MockWebServer().apply { start() }
        mockWebServerPhotos = MockWebServer().apply { start() }

        val client = OkHttpClient.Builder().build()
        val contentType = "application/json".toMediaType()

        picPayService = Retrofit.Builder()
            .baseUrl(mockWebServerPicPay.url("/"))
            .client(client)
            .addConverterFactory(JsonConfig.instance.asConverterFactory(contentType))
            .build()
            .create(PicPayService::class.java)

        personService = Retrofit.Builder()
            .baseUrl(mockWebServerPerson.url("/"))
            .client(client)
            .addConverterFactory(JsonConfig.instance.asConverterFactory(contentType))
            .build()
            .create(PersonService::class.java)

        photosService = Retrofit.Builder()
            .baseUrl(mockWebServerPhotos.url("/"))
            .client(client)
            .addConverterFactory(JsonConfig.instance.asConverterFactory(contentType))
            .build()
            .create(PhotosService::class.java)

        firebaseAuth = FirebaseAuth.getInstance()

        // --- REPOSITORIES ---
        contactUserRepository = ContactUserRepositoryImpl(
            picPayService = picPayService,
            contactUserDao = contactUserDao
        )
        userRepository = UserRepositoryImpl(
            firebaseAuth = firebaseAuth,
            userDao = userDao
        )
        peopleRepository = PeopleRepositoryImpl(
            personService = personService,
            photosService = photosService,
            peopleDao = peopleDao
        )

        startKoin {
            modules(
                module {
                    single { contactUserDao }
                    single { userDao }
                    single { peopleDao }
                    single { picPayService }
                    single { personService }
                    single { photosService }
                    single { firebaseAuth }
                    single<ContactUserRepository> { contactUserRepository }
                    single<UserRepository> { userRepository }
                    single<PeopleRepository> { peopleRepository }
                }
            )
        }
    }

    @After
    fun tearDown() {
        database.close()
        mockWebServerPicPay.shutdown()
        mockWebServerPerson.shutdown()
        mockWebServerPhotos.shutdown()
        stopKoin()
    }


    @Test
    fun contactUserRepositoryReturnsSuccess() = runBlocking {
        val jsonResponse = """
            [
                {"id":"1","name":"Alice","username":"alice01","img":"img1.jpg"},
                {"id":"2","name":"Bob","username":"bob02","img":"img2.jpg"}
            ]
        """.trimIndent()
        mockWebServerPicPay.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        when (val result = contactUserRepository.getContactUsers().first()) {
            is Result.Success -> {
                val users = result.data
                assertEquals(2, users.size)
                assertEquals("Alice", users[0].name)
            }

            else -> error("Expected Success but got $result")
        }
    }

    @Test
    fun userRepository_returnsSuccess() = runBlocking {
        val testUser = UserEntity(
            id = "uid123",
            name = "Test User",
            email = "test@example.com",
            img = "test.jpe"
        )
        userDao.insertUser(testUser)

        when (val result = userRepository.getCurrentUser().first()) {
            is Result.Success -> {
                val user = result.data
                assertEquals("uid123", user?.uid)
                assertEquals("Test User", user?.displayName)
            }

            else -> error("Expected Success but got $result")
        }
    }

    @Test
    fun peopleRepository_returnsSuccess() = runBlocking {
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
        mockWebServerPerson.enqueue(MockResponse().setBody(personJson).setResponseCode(200))
        mockWebServerPhotos.enqueue(
            MockResponse().setBody("[{\"download_url\":\"url_photo1\"}]").setResponseCode(200)
        )

        when (val result = peopleRepository.getPeople().first()) {
            is Result.Success -> {
                val people = result.data
                assertEquals(1, people.size)
                assertEquals("Ada", people[0]?.fistName)
                assertEquals("url_photo1", people[0]?.photos?.get(0)?.url)
            }

            else -> error("Expected Success but got $result")
        }
    }
}