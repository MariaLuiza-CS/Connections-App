package com.picpay.desafio.android.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Test

class PhotoTest {
    @Test
    fun `default constructor should create object with null url`() {
        val photo = Photo()

        assertNull(photo.url)
    }

    @Test
    fun `constructor should assign url correctly`() {
        val photo = Photo(url = "https://example.com/photo.jpg")

        assertEquals("https://example.com/photo.jpg", photo.url)
    }

    @Test
    fun `equality of data class should work`() {
        val p1 = Photo(url = "a")
        val p2 = Photo(url = "a")

        assertEquals(p1, p2)
        assertEquals(p1.hashCode(), p2.hashCode())
    }

    @Test
    fun `copy should create a new instance with updated field`() {
        val original = Photo(url = "old_url")
        val copy = original.copy(url = "new_url")

        assertEquals("new_url", copy.url)
        assertNotSame(original, copy)
    }
}
