package com.picpay.desafio.android.data.remote.dto

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import kotlin.test.Test

class PictureResponseDtoTest {
    @Test
    fun `picture dto copy and equality works correctly`() {
        val pic1 = PictureResponseDto(large = "https://example.com/image1.jpg")
        val pic2 = pic1.copy(large = "https://example.com/image2.jpg")

        assertEquals("https://example.com/image2.jpg", pic2.large)
        assertNotEquals(pic1, pic2)
    }

    @Test
    fun `picture dto serialization and deserialization works correctly`() {
        val pic = PictureResponseDto(large = "https://example.com/image.jpg")
        val json = Json.encodeToString(pic)
        val deserialized = Json.decodeFromString<PictureResponseDto>(json)

        assertEquals(pic, deserialized)
    }

    @Test
    fun `picture dto default values are null`() {
        val pic = PictureResponseDto()
        assertNull(pic.large)
    }
}
