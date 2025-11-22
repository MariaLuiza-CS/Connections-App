package com.picpay.desafio.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.picpay.desafio.android.presentation.contact.ContactScreen
import com.picpay.desafio.android.presentation.home.HomeScreen
import com.picpay.desafio.android.presentation.login.LoginScreen
import com.picpay.desafio.android.presentation.profile.ProfileScreen
import com.picpay.desafio.android.presentation.utils.ErrorScreen

@Composable
fun PicPayNavGraph(
    navHostController: NavHostController,
    startNavigation: Any,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController,
        startDestination = startNavigation,
        modifier = modifier
    ) {
        composable<ContactScreen> {
            ContactScreen(navHostController)
        }
        composable<ErrorScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<ErrorScreen>()

            ErrorScreen(
                navHostController = navHostController,
                messageError = args.messageError
            )
        }
        composable<LoginScreen> {
            LoginScreen(
                navHostController = navHostController
            )
        }
        composable<HomeScreen> {
            HomeScreen(navHostController)
        }
        composable<ProfileScreen> {
            ProfileScreen(navHostController)
        }
    }
}
