package com.picpay.desafio.android.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.picpay.desafio.android.presentation.home.HomeScreen
import com.picpay.desafio.android.presentation.utils.ErrorScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navHostController: NavHostController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val credentialManager = CredentialManager.create(context)

    val googleIdOption = GetGoogleIdOption.Builder()
        .setServerClientId("323443411318-qml04ht0bn5tdm8969hohdsj6aebeuh6.apps.googleusercontent.com")
        .setFilterByAuthorizedAccounts(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToError -> {
                    navHostController.navigate(
                        ErrorScreen(
                            messageError = "We encountered an error while trying to connect with our server."
                        )
                    )
                }

                is LoginEffect.NavigateToHome -> {
                    navHostController.navigate(
                        HomeScreen
                    )
                }
            }
        }
    }

    Column(Modifier.padding(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }

        Button(onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response =
                        credentialManager.getCredential(context = context, request = request)
                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(response.credential.data)
                    val idToken = googleCredential.idToken

                    viewModel.onEvent(LoginEvent.SignInGoogle(idToken))
                } catch (e: Exception) {
                    navHostController.navigate(
                        ErrorScreen(
                            messageError = e.message
                        )
                    )
                }
            }
        }) {
            Text("Entrar com Google")
        }

        uiState.error?.let {
            Text("Erro: $it")
        }
    }
}

@Serializable
object LoginScreen