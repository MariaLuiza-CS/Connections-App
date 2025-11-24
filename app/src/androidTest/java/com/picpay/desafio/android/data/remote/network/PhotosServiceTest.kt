package com.picpay.desafio.android.data.remote.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.picpay.desafio.android.data.remote.service.PhotosService
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

class PhotosServiceTest {

    object JsonConfig {
        val instance = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
    }

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PhotosService

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
    fun getPhotosReturnsListOfPhotos() = runBlocking {
        val jsonResponse = """
            [
                {"download_url": "https://example.com/photo1.jpg"},
                {"download_url": "https://example.com/photo2.jpg"}
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = service.getPhotos(page = 1, limit = 2)

        assertNotNull(response)
        assertEquals(2, response.size)
        assertEquals("https://example.com/photo1.jpg", response[0].download_url)
        assertEquals("https://example.com/photo2.jpg", response[1].download_url)
    }
}