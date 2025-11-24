package com.picpay.desafio.android.di.core

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.entity.ContactUserEntity
import com.picpay.desafio.android.data.local.entity.UserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

class DatabaseModuleIntegrationTest {

    private lateinit var database: ConnectionsAppDataBase

    @Before
    fun setup() {
        val testModule = module {
            single {
                Room.inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    ConnectionsAppDataBase::class.java
                ).allowMainThreadQueries().build()
            }
        }

        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(listOf(testModule))
        }

        database = getKoin().get()
    }

    @After
    fun teardown() {
        database.close()
        stopKoin()
    }

    @Test
    fun contactUserDaoCanInsertAndReadData() = runBlocking {
        val dao = database.contactUserDao()
        val user = ContactUserEntity("1", "Ada", "ada01", "img.jpg")
        dao.insertContactUsersList(listOf(user))

        val result = dao.getContactUsersList().first()
        assertEquals(1, result.size)
        assertEquals("Ada", result.first().name)
    }

    @Test
    fun userDaoCanInsertAndReadData() = runBlocking {
        val dao = database.userDao()
        val userEntity = UserEntity(
            id = "1",
            name = "Ada",
            email = "ada@example.com",
            img = "https://example.com/ada.jpg"
        )
        dao.insertUser(userEntity)

        val result = dao.getUser().first()
        assertNotNull(result)
        assertEquals("Ada", result?.name)
    }
}