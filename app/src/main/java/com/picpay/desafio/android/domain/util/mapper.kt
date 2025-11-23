package com.picpay.desafio.android.domain.util

import com.google.firebase.auth.FirebaseUser
import com.picpay.desafio.android.data.local.entity.ContactUserEntity
import com.picpay.desafio.android.data.local.entity.PersonWithPhotosEntity
import com.picpay.desafio.android.data.local.entity.PhotoEntity
import com.picpay.desafio.android.data.local.entity.UserEntity
import com.picpay.desafio.android.data.remote.dto.ContactUserResponseDto
import com.picpay.desafio.android.domain.model.ContactUser
import com.picpay.desafio.android.domain.model.PersonWithPhotos
import com.picpay.desafio.android.domain.model.Photo
import com.picpay.desafio.android.domain.model.User

fun ContactUserEntity.toUser() =
    User(
        id = id,
        name = name,
        img = img,
        username = username
    )

fun ContactUserResponseDto.toUserEntity() =
    ContactUserEntity(
        id = id ?: "",
        name = name,
        img = img,
        username = username
    )

fun FirebaseUser.toAuthenticationPersonEntity() =
    UserEntity(
        id = uid,
        name = displayName ?: "",
        email = email ?: "",
        img = photoUrl.toString()
    )

fun UserEntity.toAuthenticationUser() =
    ContactUser(
        id = id,
        name = name ?: "",
        email = email ?: "",
        img = img ?: ""
    )

fun PersonWithPhotosEntity.toPersonWithPhotos() =
    PersonWithPhotos(
        id = person.id,
        fistName = person.fistName,
        lastName = person.lastName,
        email = person.email,
        title = person.title,
        gender = person.gender,
        profilePicture = person.profilePicture,
        photos = photos.map { it.toPhoto() }
    )

fun PhotoEntity.toPhoto() =
    Photo(
        url = url
    )