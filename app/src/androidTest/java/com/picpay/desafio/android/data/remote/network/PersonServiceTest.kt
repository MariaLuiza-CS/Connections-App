package com.picpay.desafio.android.data.remote.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.picpay.desafio.android.data.remote.service.PersonService
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
import retrofit2.Retrofit
import retrofit2.create

class PersonServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PersonService

    object JsonConfig {
        val instance = Json { ignoreUnknownKeys = true }
    }

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
    fun getPeopleReturnsListOfPeople() = runBlocking {
        val jsonResponse = """
            {
              "results": [
                {
                  "gender": "female",
                  "name": {"title": "Ms", "first": "Ada", "last": "Lovelace"},
                  "email": "ada@example.com",
                  "picture": {"large": "url_image"},
                  "login": {"uuid": "uuid-123"}
                },
                {
                  "gender": "male",
                  "name": {"title": "Mr", "first": "Bob", "last": "Johnson"},
                  "email": "bob@example.com",
                  "picture": {"large": "url_image2"},
                  "login": {"uuid": "uuid-456"}
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        val response = service.getPeople()

        assertNotNull(response.results)
        assertEquals(2, response.results?.size)

        val firstPerson = response.results?.first()
        assertEquals("Ada", firstPerson?.name?.first)
        assertEquals("uuid-123", firstPerson?.login?.uuid)

        val secondPerson = response.results?.get(1)
        assertEquals("Bob", secondPerson?.name?.first)
        assertEquals("uuid-456", secondPerson?.login?.uuid)
    }
}
