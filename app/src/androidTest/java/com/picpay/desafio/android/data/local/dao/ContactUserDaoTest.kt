package com.picpay.desafio.android.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.entity.ContactUserEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ContactUserDaoTest {
    private lateinit var database: ConnectionsAppDataBase
    private lateinit var dao: ContactUserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConnectionsAppDataBase::class.java
        ).allowMainThreadQueries()
            .build()

        dao = database.contactUserDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertContactUsersListAndGetContactUsersListReturnsInsertedData() = runBlocking {
        val users = listOf(
            ContactUserEntity(id = "1", name = "User 1", username = "user1", img = null),
            ContactUserEntity(id = "2", name = "User 2", username = "user2", img = "img2.jpg")
        )

        dao.insertContactUsersList(users)

        val result = dao.getContactUsersList().first()
        assertEquals(users, result)
    }

    @Test
    fun insertContactUsersListOnConflictReplacesData() = runBlocking {
        val user = ContactUserEntity(id = "1", name = "Original", username = "Original", img = null)
        dao.insertContactUsersList(listOf(user))

        val updatedUser = ContactUserEntity(id = "1", name = "Updated", username = "Updated", img = "img.jpg")
        dao.insertContactUsersList(listOf(updatedUser))

        val result = dao.getContactUsersList().first()
        assertEquals(listOf(updatedUser), result)
    }

    @Test
    fun cleanContactUsersListClearsAllData() = runBlocking {
        val users = listOf(
            ContactUserEntity(id = "1", name = "User 1", username = "user1", img = null),
            ContactUserEntity(id = "2", name = "User 2", username = "user2", img = "img2.jpg")
        )

        dao.insertContactUsersList(users)
        dao.cleanContactUsersList()

        val result = dao.getContactUsersList().first()
        assertTrue(result.isEmpty())
    }
}
