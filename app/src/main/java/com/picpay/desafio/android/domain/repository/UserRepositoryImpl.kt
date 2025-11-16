package com.picpay.desafio.android.domain.repository

import com.picpay.desafio.android.data.local.UserDao
import com.picpay.desafio.android.data.remote.PicPayService
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.domain.model.Result
import com.picpay.desafio.android.domain.model.User
import com.picpay.desafio.android.domain.util.toUser
import com.picpay.desafio.android.domain.util.toUserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val picPayService: PicPayService,
    private val userDao: UserDao
) : UserRepository {
    override fun getUsers(): Flow<Result<List<User>>> = flow {
        emit(Result.Loading)

        val getLocalUsers = userDao.getUsers()

        var hasLocalUsers = false

        val initialLocalUsers = getLocalUsers.firstOrNull().orEmpty()

        if (initialLocalUsers.isNotEmpty()) {
            hasLocalUsers = true
            emit(
                Result.Success(
                    initialLocalUsers.map {
                        it.toUser()
                    }
                )
            )
        }

        try {
            val usersFromApi = picPayService.getUsers()
                .map {
                    it.toUserEntity()
                }
            userDao.insertUsers(usersFromApi)
        } catch (e: Exception) {
            if (!hasLocalUsers) {
                emit(
                    Result.Error(
                        exception = e,
                        message = e.message
                    )
                )
            }
        }

        getLocalUsers.collect { entities ->
            if (entities.isNotEmpty()) {
                emit(
                    Result.Success(
                        entities.map {
                            it.toUser()
                        }
                    )
                )
            }

        }
    }
}
