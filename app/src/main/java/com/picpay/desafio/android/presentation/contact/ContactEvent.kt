package com.picpay.desafio.android.presentation.contact

sealed class ContactEvent {
    data object LoadUsersList: ContactEvent()
}
