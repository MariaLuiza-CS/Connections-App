package com.picpay.desafio.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class PicPayDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
