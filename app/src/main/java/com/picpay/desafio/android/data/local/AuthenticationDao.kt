package com.picpay.desafio.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthenticationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthenticationPerson(authenticationPersonEntity: AuthenticationPersonEntity)

    @Query("SELECT * FROM authentication_person")
    fun getAuthenticationPerson(): Flow<AuthenticationPersonEntity?>

    @Query("DELETE FROM authentication_person")
    suspend fun cleanAuthenticationPerson()
}
