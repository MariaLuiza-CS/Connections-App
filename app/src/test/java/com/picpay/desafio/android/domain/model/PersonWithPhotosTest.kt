package com.picpay.desafio.android.domain.model

import org.junit.Assert.assertNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PersonWithPhotosTest {
    @Test
    fun `when creating PersonWithPhotos with no parameters, all fields should be null or empty`() {
        val person = PersonWithPhotos()

        assertNull(person.id)
        assertNull(person.fistName)
        assertNull(person.lastName)
        assertNull(person.title)
        assertNull(person.gender)
        assertNull(person.email)
        assertNull(person.profilePicture)
        assertNotNull(person.photos)
        assertTrue(person.photos.isEmpty())
    }

    @Test
    fun `creating PersonWithPhotos with values should keep fields correctly`() {
        val photosList = listOf(
            Photo("p1"),
            null
        )

        val person = PersonWithPhotos(
            id = "123",
            fistName = "Maria",
            lastName = "Silva",
            title = "Dr.",
            gender = "F",
            email = "maria@example.com",
            profilePicture = "profile_url",
            photos = photosList
        )

        assertEquals("123", person.id)
        assertEquals("Maria", person.fistName)
        assertEquals("Silva", person.lastName)
        assertEquals("Dr.", person.title)
        assertEquals("F", person.gender)
        assertEquals("maria@example.com", person.email)
        assertEquals("profile_url", person.profilePicture)
        assertSame(photosList, person.photos)
    }

    @Test
    fun `data class equality should work`() {
        val p1 = PersonWithPhotos(
            id = "1",
            fistName = "A",
            lastName = "B",
            photos = listOf(Photo("p"))
        )
        val p2 = PersonWithPhotos(
            id = "1",
            fistName = "A",
            lastName = "B",
            photos = listOf(Photo("p"))
        )

        assertEquals(p1, p2)
        assertEquals(p1.hashCode(), p2.hashCode())
    }

    @Test
    fun `copy should create a new instance with updated values`() {
        val original = PersonWithPhotos(id = "1", fistName = "John")
        val copy = original.copy(fistName = "Peter")

        assertEquals("1", copy.id)
        assertEquals("Peter", copy.fistName)
        assertNotSame(original, copy)
    }
}
