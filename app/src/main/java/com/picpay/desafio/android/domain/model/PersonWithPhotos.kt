package com.picpay.desafio.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonWithPhotos(
    val id: String? = null,
    val fistName: String? = null,
    val lastName: String? = null,
    val title: String? = null,
    val gender: String? = null,
    val email: String? = null,
    val profilePicture: String? = null,
    val photos: List<Photo?> = emptyList()
) : Parcelable
