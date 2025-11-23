package com.picpay.desafio.android.presentation.login

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUiState(
    var isLoading: Boolean = false,
    var error: String? = null
) : Parcelable
