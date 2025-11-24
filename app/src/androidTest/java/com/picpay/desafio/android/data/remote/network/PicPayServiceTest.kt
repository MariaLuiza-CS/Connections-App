package com.picpay.desafio.android.data.remote.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.picpay.desafio.android.data.remote.service.PicPayService
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.create

class PicPayServiceTest {

    object JsonConfig {
        val instance = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PicPayService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val client = OkHttpClient.Builder().build()
        val baseUrl = mockWebServer.url("/").toString()

        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(JsonConfig.instance.asConverterFactory(contentType))
            .build()

        service = retrofit.create()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getContactUsersReturnsListOfUsers() = runBlocking {
        val jsonResponse = """
            [
                {"id": "1", "name": "Ada", "username": "ada01", "img": "img1.jpg"},
                {"id": "2", "name": "Bob", "username": "bob02", "img": "img2.jpg"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = service.getContactUsers()

        assertNotNull(response)
        assertEquals(2, response.size)
        assertEquals("Ada", response[0].name)
        assertEquals("ada01", response[0].username)
        assertEquals("img1.jpg", response[0].img)

        assertEquals("Bob", response[1].name)
        assertEquals("bob02", response[1].username)
        assertEquals("img2.jpg", response[1].img)
    }
}
