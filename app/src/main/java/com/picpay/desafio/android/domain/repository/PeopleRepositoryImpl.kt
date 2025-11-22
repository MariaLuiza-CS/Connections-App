package com.picpay.desafio.android.domain.repository

import com.picpay.desafio.android.data.local.dao.PeopleDao
import com.picpay.desafio.android.data.local.entity.PersonEntity
import com.picpay.desafio.android.data.local.entity.PersonWithPhotosEntity
import com.picpay.desafio.android.data.local.entity.PhotoEntity
import com.picpay.desafio.android.data.remote.service.PersonService
import com.picpay.desafio.android.data.remote.service.PhotosService
import com.picpay.desafio.android.data.repository.PeopleRepository
import com.picpay.desafio.android.domain.model.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class PeopleRepositoryImpl(
    private val personService: PersonService,
    private val photosService: PhotosService,
    private val peopleDao: PeopleDao
) : PeopleRepository {
    override fun getPeople(): Flow<Result<List<PersonWithPhotosEntity?>>> = flow {
        emit(Result.Loading)

        val localPeopleList = peopleDao.getAllPeopleWithPhotos()
        val initialLocalPerson = localPeopleList.firstOrNull().orEmpty()
        val hasLocalPerson = initialLocalPerson.isNotEmpty()
        val semaphore = Semaphore(permits = 4)

        if (hasLocalPerson) {
            emit(
                Result.Success(
                    initialLocalPerson
                )
            )
        }

        try {
            val peopleFromApi = personService.getPeople(20)
            val apiPeopleList = peopleFromApi.results.orEmpty()

            coroutineScope {
                val jobs = apiPeopleList.map { person ->
                    async {
                        semaphore.withPermit {
                            val randomPage = (1..10).random()
                            val randomLimit = (1..30).random()

                            val photosFromApi = photosService.getPhotos(
                                page = randomPage,
                                limit = randomLimit
                            )

                            val personEntity = PersonEntity(
                                id = person.login?.uuid ?: "",
                                fistName = person.name?.first ?: "",
                                lastName = person.name?.last ?: "",
                                title = person.name?.title ?: "",
                                email = person.email ?: "",
                                gender = person.gender ?: "",
                                profilePicture = person.picture?.large ?: ""
                            )

                            val photoEntities = photosFromApi.map { photo ->
                                PhotoEntity(
                                    personId = "",
                                    url = photo.download_url ?: ""
                                )
                            }

                            peopleDao.insertPersonWithPhotos(personEntity, photoEntities)
                        }
                    }
                }

                jobs.awaitAll()
            }
        } catch (e: Exception) {
            if (!hasLocalPerson) {
                emit(
                    Result.Error(
                        exception = e,
                        message = e.message
                    )
                )
            }
        }

        localPeopleList.collect { entities ->
            if (entities.isNotEmpty()) {
                emit(
                    Result.Success(
                        entities
                    )
                )
            }
        }
    }
}
