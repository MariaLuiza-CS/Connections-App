package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<Result<List<User>>>
}
