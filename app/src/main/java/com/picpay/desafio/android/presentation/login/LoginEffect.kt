package com.picpay.desafio.android.presentation.login

sealed class LoginEffect {
    data class NavigateToError(val messageError: String) : LoginEffect()
    object NavigateToHome : LoginEffect()
}