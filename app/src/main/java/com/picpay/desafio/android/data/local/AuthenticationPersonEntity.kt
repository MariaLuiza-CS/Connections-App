package com.picpay.desafio.android.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "authentication_person")
data class AuthenticationPersonEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val email: String?,
    val img: String?
)
