package com.picpay.desafio.android.presentation.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.picpay.desafio.android.presentation.navigation.ConnectionAppScarffold
import com.picpay.desafio.android.ui.PicPayTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        enableEdgeToEdge()

        setContent {
            PicPayTheme {
                val viewModel: MainViewModel = koinViewModel()
                val uiState by viewModel.uiState.collectAsState()
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<InitialScreen?>(null) }

                viewModel.onEvent(MainEvent.getLocalCurrentUser)
                viewModel.onEvent(MainEvent.getPeopleWithPhotos)

                splashScreen.setKeepOnScreenCondition {
                    uiState.isLoading
                }

                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is MainEffect.NavigateToHome -> {
                                startDestination = InitialScreen.HomeScreenRoute
                            }

                            is MainEffect.NavigateToLogin -> {
                                startDestination = InitialScreen.LoginScreenRoute
                            }
                        }
                    }
                }

                if (startDestination != null) {
                    ConnectionAppScarffold(
                        navController,
                        startDestination!!.route
                    )
                }
            }
        }
    }
}
