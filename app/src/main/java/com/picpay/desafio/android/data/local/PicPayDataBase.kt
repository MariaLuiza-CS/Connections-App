package com.picpay.desafio.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, AuthenticationPersonEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PicPayDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun authenticationDao(): AuthenticationDao
}
