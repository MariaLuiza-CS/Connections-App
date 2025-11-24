package com.picpay.desafio.android.data.remote.dto

import junit.framework.TestCase.assertEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertNotEquals
import kotlin.test.Test
import kotlin.test.assertNull

class PersonResponseDtoTest {
    @Test
    fun `person dto copy and equality works correctly`() {
        val name = NameResponseDto(title = "Mr", first = "Ada", last = "Lovelace")
        val picture = PictureResponseDto(large = "https://example.com/ada.jpg")
        val login = LoginResponseDto(uuid = "uuid-123")

        val dto1 = PersonResponseDto(
            gender = "female",
            name = name,
            email = "ada@example.com",
            picture = picture,
            login = login
        )

        val dto2 = dto1.copy(email = "ada2@example.com")

        assertEquals("female", dto2.gender)
        assertEquals("ada2@example.com", dto2.email)
        assertEquals(dto1.name, dto2.name)
        assertEquals(dto1.picture, dto2.picture)
        assertNotEquals(dto1, dto2)
    }

    @Test
    fun `person dto serialization and deserialization works correctly`() {
        val name = NameResponseDto(title = "Mr", first = "Ada", last = "Lovelace")
        val picture = PictureResponseDto(large = "https://example.com/ada.jpg")
        val login = LoginResponseDto(uuid = "uuid-123")

        val dto = PersonResponseDto(
            gender = "female",
            name = name,
            email = "ada@example.com",
            picture = picture,
            login = login
        )

        val json = Json.encodeToString(dto)
        val deserialized = Json.decodeFromString<PersonResponseDto>(json)

        assertEquals(dto, deserialized)
    }

    @Test
    fun `person dto default values are null`() {
        val dto = PersonResponseDto()
        assertNull(dto.gender)
        assertNull(dto.name)
        assertNull(dto.email)
        assertNull(dto.picture)
        assertNull(dto.login)
    }

    @Test
    fun `nested dto default values are null`() {
        val name = NameResponseDto()
        val picture = PictureResponseDto()
        val login = LoginResponseDto()

        assertNull(name.title)
        assertNull(name.first)
        assertNull(name.last)

        assertNull(picture.large)

        assertNull(login.uuid)
    }
}
