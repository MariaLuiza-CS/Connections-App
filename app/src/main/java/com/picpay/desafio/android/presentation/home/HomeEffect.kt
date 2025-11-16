package com.picpay.desafio.android.presentation.home

sealed class HomeEffect {
    data class NavigateToError(val message: String) : HomeEffect()
}