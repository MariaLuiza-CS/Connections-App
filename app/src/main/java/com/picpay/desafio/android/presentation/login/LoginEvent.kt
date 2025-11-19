package com.picpay.desafio.android.presentation.login

sealed class LoginEvent {
    data class SignInGoogle(val idToken: String) : LoginEvent()
}