package com.picpay.desafio.android.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.picpay.desafio.android.presentation.utils.BottomBar
import kotlinx.serialization.Serializable

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    ProfileScreenContent(
        navHostController = navHostController,
        modifier = modifier
    )
}

@Serializable
object ProfileScreen