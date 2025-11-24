package com.picpay.desafio.android.domain

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.local.ConnectionsAppDataBase
import com.picpay.desafio.android.data.local.dao.UserDao
import com.picpay.desafio.android.data.local.entity.UserEntity
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever

object TestUtils {
    fun createFakeUserEntity(id: String) =
        UserEntity(
            id = id,
            name = "Test User",
            email = "email@test.com",
            img = "img.jpg"
        )
}

class UserRepositoryImplInstrumentedTest {

    private lateinit var database: ConnectionsAppDataBase
    private lateinit var userDao: UserDao
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ConnectionsAppDataBase::class.java
        ).build()

        userDao = database.userDao()
        firebaseAuth = Mockito.mock(FirebaseAuth::class.java)
        repository = UserRepositoryImpl(firebaseAuth, userDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getLocalCurrentUser_returns_loading_then_success() = runTest {
        val fakeUser = TestUtils.createFakeUserEntity("123")
        userDao.insertUser(fakeUser)
        val emissions = repository.getLocalCurrentUser().first()
        assertTrue(emissions is com.picpay.desafio.android.domain.model.Result.Success)
    }

    @Test
    fun getCurrentUser_success_emits_user() = runTest {
        val firebaseUser = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
        val result = repository.getCurrentUser().first()
        assertTrue(result is com.picpay.desafio.android.domain.model.Result.Success)
        assertEquals(
            firebaseUser,
            (result as com.picpay.desafio.android.domain.model.Result.Success).data
        )
    }

    @Test
    fun getCurrentUser_failure_emits_error() = runTest {
        Mockito.`when`(firebaseAuth.currentUser).thenThrow(RuntimeException("Fail"))
        val result = repository.getCurrentUser().first()
        assertTrue(result is com.picpay.desafio.android.domain.model.Result.Error)
    }

    @Test
    fun signInWithGoogle_success_inserts_user_in_dao() = runTest {
        val firebaseUser = Mockito.mock(FirebaseUser::class.java)
        val authResult = Mockito.mock(AuthResult::class.java)
        Mockito.`when`(authResult.user).thenReturn(firebaseUser)
        whenever(firebaseAuth.signInWithCredential(Mockito.any(AuthCredential::class.java)))
            .thenReturn(Tasks.forResult(authResult))

        val lastEmission = repository.signInWithGoogle("TOKEN").first()
        assertTrue(lastEmission is com.picpay.desafio.android.domain.model.Result.Success)
        val dbUser = userDao.getUser().first()
        assertNotNull(dbUser)
    }

    @Test
    fun signInWithGoogle_failure_emits_error() = runTest {
        whenever(firebaseAuth.signInWithCredential(Mockito.any(AuthCredential::class.java)))
            .thenReturn(Tasks.forException(RuntimeException("Login failed")))
        val result = repository.signInWithGoogle("TOKEN").first()
        assertTrue(result is Result.Error)
    }

    @Test
    fun signOut_calls_FirebaseAuth_signOut() {
        repository.signOut()
        Mockito.verify(firebaseAuth).signOut()
    }
}