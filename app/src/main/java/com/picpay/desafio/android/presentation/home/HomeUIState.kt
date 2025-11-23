package com.picpay.desafio.android.presentation.home

import android.os.Parcelable
import com.picpay.desafio.android.domain.model.PersonWithPhotos
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeUIState(
    var isLoading: Boolean = false,
    var peopleList: List<PersonWithPhotos?> = emptyList(),
    var error: String? = null
) : Parcelable
