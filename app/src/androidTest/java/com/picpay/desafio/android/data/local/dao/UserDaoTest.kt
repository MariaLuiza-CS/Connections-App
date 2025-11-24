package com.picpay.desafio.android.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.entity.UserEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserDaoTest {
    private lateinit var database: ConnectionsAppDataBase
    private lateinit var dao: UserDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConnectionsAppDataBase::class.java
        ).allowMainThreadQueries()
            .build()

        dao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUserAndGetUserReturnsInsertedUser() = runBlocking {
        val user = UserEntity(
            id = "1",
            name = "Ada",
            email = "ada@example.com",
            img = "https://example.com/ada.jpg"
        )

        dao.insertUser(user)
        val result = dao.getUser().first()

        assertNotNull(result)
        assertEquals(user.id, result?.id)
        assertEquals(user.name, result?.name)
        assertEquals(user.email, result?.email)
        assertEquals(user.img, result?.img)
    }

    @Test
    fun insertUserOnConflict_replacesUser() = runBlocking {
        val originalUser = UserEntity(
            id = "1",
            name = "Ada",
            email = "ada@example.com",
            img = "https://example.com/ada.jpg"
        )
        dao.insertUser(originalUser)

        val updatedUser = UserEntity(
            id = "1",
            name = "AdaUpdated",
            email = "ada.updated@example.com",
            img = "https://example.com/ada.updated.jpg"
        )
        dao.insertUser(updatedUser)

        val result = dao.getUser().first()
        assertNotNull(result)
        assertEquals("AdaUpdated", result?.name)
        assertEquals("ada.updated@example.com", result?.email)
        assertEquals("https://example.com/ada.updated.jpg", result?.img)
    }

    @Test
    fun cleanUser_removesUser() = runBlocking {
        val user = UserEntity(
            id = "1",
            name = "Ada",
            email = "ada@example.com",
            img = "https://example.com/ada.jpg"
        )
        dao.insertUser(user)

        dao.cleanUser()
        val result = dao.getUser().first()
        assertNull(result)
    }
}