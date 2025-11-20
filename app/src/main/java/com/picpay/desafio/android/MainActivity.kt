package com.picpay.desafio.android

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import com.picpay.desafio.android.presentation.login.LoginScreen
import com.picpay.desafio.android.ui.PicPayTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PicPayTheme {
                val navController = rememberNavController()

                PicPayNavGraph(
                    navController,
                    LoginScreen
                )
            }
        }
    }
}
