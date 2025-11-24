package com.picpay.desafio.android.presentation.home

sealed class HomeEvent {
    data object LoadPeopleList : HomeEvent()
}
