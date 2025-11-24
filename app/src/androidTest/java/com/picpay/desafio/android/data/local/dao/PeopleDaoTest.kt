package com.picpay.desafio.android.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.entity.PersonEntity
import com.picpay.desafio.android.data.local.entity.PhotoEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class PeopleDaoTest {

    private lateinit var database: ConnectionsAppDataBase
    private lateinit var dao: PeopleDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConnectionsAppDataBase::class.java
        ).allowMainThreadQueries()
            .build()

        dao = database.peopleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPersonWithPhotosAndGetAllPeopleWithPhotosReturnsCorrectData() = runBlocking {
        val person = PersonEntity(
            id = "1",
            fistName = "Ada",
            lastName = "Lovelace",
            title = "Ms",
            gender = "Female",
            email = "ada@example.com",
            profilePicture = "https://example.com/ada.jpg"
        )

        val photos = listOf(
            PhotoEntity(personId = "", url = "https://example.com/photo1.jpg"),
            PhotoEntity(personId = "", url = "https://example.com/photo2.jpg")
        )

        dao.insertPersonWithPhotos(person, photos)

        val result = dao.getAllPeopleWithPhotos().first()
        assertEquals(1, result.size)

        val insertedPersonWithPhotos = result[0]

        assertEquals(person.id, insertedPersonWithPhotos.person.id)
        assertEquals(person.fistName, insertedPersonWithPhotos.person.fistName)
        assertEquals(2, insertedPersonWithPhotos.photos.size)
        assertEquals("https://example.com/photo1.jpg", insertedPersonWithPhotos.photos[0].url)
        assertEquals("https://example.com/photo2.jpg", insertedPersonWithPhotos.photos[1].url)
    }

    @Test
    fun insertPersonOnConflictReplacesPerson() = runBlocking {
        val originalPerson = PersonEntity(
            id = "1",
            fistName = "Ada",
            lastName = "Lovelace",
            title = "Ms",
            gender = "Female",
            email = "ada@example.com",
            profilePicture = "https://example.com/ada.jpg"
        )
        dao.insertPerson(originalPerson)

        val updatedPerson = PersonEntity(
            id = "1",
            fistName = "AdaUpdated",
            lastName = "LovelaceUpdated",
            title = "Ms",
            gender = "Female",
            email = "ada.Updated@example.com",
            profilePicture = "https://example.com/ada-Updated.jpg"
        )
        dao.insertPerson(updatedPerson)

        val result = dao.getAllPeopleWithPhotos().first()
        assertEquals(1, result.size)
        assertEquals("AdaUpdated", result[0].person.fistName)
        assertEquals("ada.Updated@example.com", result[0].person.email)
    }

    @Test
    fun clearPeopleRemovesAllData() = runBlocking {
        val person = PersonEntity(
            id = "1",
            fistName = "Ada",
            lastName = "Lovelace",
            title = "Ms",
            gender = "Female",
            email = "ada@example.com",
            profilePicture = "https://example.com/ada.jpg"
        )

        val photos = listOf(
            PhotoEntity(personId = "", url = "https://example.com/photo1.jpg"),
            PhotoEntity(personId = "", url = "https://example.com/photo2.jpg")
        )

        dao.insertPersonWithPhotos(person, photos)
        dao.clearPeople()

        val result = dao.getAllPeopleWithPhotos().first()
        assertTrue(result.isEmpty())
    }
}
