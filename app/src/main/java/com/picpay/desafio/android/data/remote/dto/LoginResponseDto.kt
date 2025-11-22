package com.picpay.desafio.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    var uuid: String? = null
)
