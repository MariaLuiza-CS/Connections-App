package com.picpay.desafio.android.presentation.home

import android.os.Parcelable
import com.picpay.desafio.android.domain.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
) : Parcelable
