package com.picpay.desafio.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String? = null,
    val name: String? = null,
    val img: String? = null,
    val username: String? = null,
) : Parcelable
