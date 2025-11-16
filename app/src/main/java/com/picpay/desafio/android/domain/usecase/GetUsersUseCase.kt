package com.picpay.desafio.android.domain.usecase

import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.model.User
import kotlinx.coroutines.flow.Flow

class GetUsersUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Result<List<User>>> {
        return userRepository.getUsers()
    }
}
