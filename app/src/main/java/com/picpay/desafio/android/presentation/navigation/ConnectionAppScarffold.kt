package com.picpay.desafio.android.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.picpay.desafio.android.presentation.home.HomeScreen
import com.picpay.desafio.android.presentation.profile.ProfileScreen
import com.picpay.desafio.android.presentation.utils.BottomBar

@Composable
fun ConnectionAppScarffold(
    navHostController: NavHostController,
    startNavigation: Any
) {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val homeRoute = HomeScreen::class.qualifiedName
    val profileRoute = ProfileScreen::class.qualifiedName

    val showBottomBar = currentRoute == homeRoute ||
            currentRoute == profileRoute

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navHostController = navHostController)
            }
        }
    ) { innerPadding ->
        PicPayNavGraph(
            navHostController = navHostController,
            startNavigation = startNavigation,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
