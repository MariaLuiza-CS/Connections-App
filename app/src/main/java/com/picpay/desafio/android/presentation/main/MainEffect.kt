package com.picpay.desafio.android.presentation.main

import com.picpay.desafio.android.presentation.home.HomeEffect

sealed class MainEffect {
    object NavigateToHome : MainEffect()
    object NavigateToLogin : MainEffect()
}