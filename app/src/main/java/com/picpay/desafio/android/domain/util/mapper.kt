package com.picpay.desafio.android.domain.util

import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.local.AuthenticationPersonEntity
import com.picpay.desafio.android.data.local.UserEntity
import com.picpay.desafio.android.data.remote.dto.UserResponseDto
import com.picpay.desafio.android.domain.model.AuthenticationUser
import com.picpay.desafio.android.domain.model.User

fun UserEntity.toUser() =
    User(
        id = id,
        name = name,
        img = img,
        username = username
    )

fun UserResponseDto.toUserEntity() =
    UserEntity(
        id = id ?: "",
        name = name,
        img = img,
        username = username
    )

fun FirebaseUser.toAuthenticationPersonEntity() =
    AuthenticationPersonEntity(
        id = uid,
        name = displayName ?: "",
        email = email ?: "",
        img = photoUrl.toString()
    )

fun AuthenticationPersonEntity.toAuthenticationUser() =
    AuthenticationUser(
        id = id,
        name = name ?: "",
        email = email ?: "",
        img = img ?: ""
    )
