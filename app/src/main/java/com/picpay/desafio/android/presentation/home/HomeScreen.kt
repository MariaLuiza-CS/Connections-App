package com.picpay.desafio.android.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

@Composable
fun HomeScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    HomeScreenContent(
        navHostController = navHostController,
        modifier = modifier
    )

}

@Serializable
object HomeScreen