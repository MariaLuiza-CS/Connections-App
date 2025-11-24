package com.picpay.desafio.android.di.core

import androidx.test.core.app.ApplicationProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.picpay.desafio.android.data.remote.service.PicPayService
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import retrofit2.Retrofit

class NetworkModuleIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var picPayService: PicPayService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val testModule = module {
            single { OkHttpClient.Builder().build() }
            single<PicPayService> {
                val contentType = "application/json".toMediaType()
                val jsonConfig = Json { ignoreUnknownKeys = true }

                Retrofit.Builder()
                    .baseUrl(mockWebServer.url("/"))
                    .client(get())
                    .addConverterFactory(jsonConfig.asConverterFactory(contentType))
                    .build()
                    .create(PicPayService::class.java)
            }
        }

        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(testModule))
        }

        picPayService = getKoin().get()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        stopKoin()
    }

    @Test
    fun picPayServiceReturnsListOfUsers () = runBlocking {
        val jsonResponse = """
            [
                {"id":"1","name":"Ada","username":"ada01","img":"img1.jpg"},
                {"id":"2","name":"Bob","username":"bob02","img":"img2.jpg"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = picPayService.getContactUsers()

        assertNotNull(response)
        assertEquals(2, response.size)
        assertEquals("Ada", response[0].name)
        assertEquals("Bob", response[1].name)
    }
}