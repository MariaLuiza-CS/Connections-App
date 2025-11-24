package com.picpay.desafio.android.data.remote.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import kotlin.test.Test

class LoginResponseDtoTest {
    @Test
    fun `login dto copy and equality works correctly`() {
        val login1 = LoginResponseDto(uuid = "uuid-123")
        val login2 = login1.copy(uuid = "uuid-456")

        assertEquals("uuid-456", login2.uuid)
        assertNotEquals(login1, login2)
    }

    @Test
    fun `login dto serialization and deserialization works correctly`() {
        val login = LoginResponseDto(uuid = "uuid-789")
        val json = Json.encodeToString(login)
        val deserialized = Json.decodeFromString<LoginResponseDto>(json)

        assertEquals(login, deserialized)
    }

    @Test
    fun `login dto default values are null`() {
        val login = LoginResponseDto()
        assertNull(login.uuid)
    }
}
