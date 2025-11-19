package com.picpay.desafio.android.di.core

import androidx.room.Room
import com.picpay.desafio.android.data.local.AuthenticationDao
import com.picpay.desafio.android.data.local.PicPayDataBase
import com.picpay.desafio.android.data.local.UserDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<PicPayDataBase> {
        Room.databaseBuilder(
            androidContext(),
            PicPayDataBase::class.java,
            "picpay_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<UserDao> {
        get<PicPayDataBase>().userDao()
    }

    single<AuthenticationDao> {
        get<PicPayDataBase>().authenticationDao()
    }
}
