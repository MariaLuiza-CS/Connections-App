package com.picpay.desafio.android.presentation.profile

import android.os.Parcelable
import com.picpay.desafio.android.domain.model.ContactUser
import com.picpay.desafio.android.domain.model.PersonWithPhotos
import com.picpay.desafio.android.domain.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileUiState(
    val isLoading: Boolean? = false,
    val currentUser: ContactUser? = null,
    val contactUsersList: List<User?> = emptyList(),
    val peopleWithPhotosList: List<PersonWithPhotos?> = emptyList(),
    val error: String? = null
) : Parcelable
