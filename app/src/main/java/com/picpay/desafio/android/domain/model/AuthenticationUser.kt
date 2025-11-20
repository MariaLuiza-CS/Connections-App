package com.picpay.desafio.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthenticationUser(
    val id: String,
    val name: String,
    val email: String,
    val img: String
) : Parcelable
