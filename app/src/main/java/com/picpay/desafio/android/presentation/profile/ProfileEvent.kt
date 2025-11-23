package com.picpay.desafio.android.presentation.profile

sealed class ProfileEvent {
    object loadCurrentUser : ProfileEvent()
    object loadContactUserList : ProfileEvent()
}
