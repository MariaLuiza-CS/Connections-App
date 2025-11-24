package com.picpay.desafio.android.presentation.main

sealed class MainEffect {
    data object NavigateToHome : MainEffect()
    data object NavigateToLogin : MainEffect()
}
