package com.picpay.desafio.android.presentation.main

sealed class MainEvent {
    data object GetLocalCurrentUser : MainEvent()
}
