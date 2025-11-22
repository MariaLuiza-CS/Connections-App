package com.picpay.desafio.android.presentation.main

sealed class MainEvent {
    object getLocalCurrentUser : MainEvent()
    object getPeopleWithPhotos : MainEvent()
}