package com.picpay.desafio.android.presentation.main

import com.picpay.desafio.android.presentation.home.HomeScreen
import com.picpay.desafio.android.presentation.login.LoginScreen

sealed class InitialScreen(val route: Any) {
    data object LoginScreenRoute : InitialScreen(LoginScreen)
    data object HomeScreenRoute : InitialScreen(HomeScreen)
}
