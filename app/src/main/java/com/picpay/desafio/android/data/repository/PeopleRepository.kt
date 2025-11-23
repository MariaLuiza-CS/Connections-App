package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.domain.model.PersonWithPhotos
import com.picpay.desafio.android.domain.model.Result
import kotlinx.coroutines.flow.Flow


interface PeopleRepository {
    fun getPeople(): Flow<Result<List<PersonWithPhotos?>>>
}
