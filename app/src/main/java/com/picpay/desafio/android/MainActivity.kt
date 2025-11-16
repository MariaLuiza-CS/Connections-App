package com.picpay.desafio.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.picpay.desafio.android.presentation.home.HomeScreen
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
                    HomeScreen
                )
            }
        }
    }
}
