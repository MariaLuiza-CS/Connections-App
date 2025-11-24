package com.picpay.desafio.android.di.core

import com.picpay.desafio.android.data.local.dao.ContactUserDao
import com.picpay.desafio.android.data.local.dao.PeopleDao
import com.picpay.desafio.android.data.local.dao.UserDao
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test

class DatabaseModuleUnitTest : KoinTest {
    private val fakeDatabaseModule = module {
        single { mockk<ContactUserDao>(relaxed = true) }
        single { mockk<UserDao>(relaxed = true) }
        single { mockk<PeopleDao>(relaxed = true) }
    }

    @Before
    fun setup() {
        startKoin {
            modules(listOf(fakeDatabaseModule))
        }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `database module provides all DAOs`() {
        val contactUserDao: ContactUserDao by inject()
        val userDao: UserDao by inject()
        val peopleDao: PeopleDao by inject()

        assertNotNull(contactUserDao)
        assertNotNull(userDao)
        assertNotNull(peopleDao)
    }
}
