package ru.dekabrsky.feature.notifications.implementation.domain.interactor

import ru.dekabrsky.feature.notifications.implementation.data.repository.RestOfPillRepository
import ru.dekabrsky.feature.notifications.implementation.domain.model.RestOfPillEntity
import javax.inject.Inject

class RestOfPillInteractor @Inject constructor(
    private val repository: RestOfPillRepository
) {
    fun getRestOfPillList(userId: Long) =
        repository.getRestOfPillList(userId)

    fun addRestOfPill(userId: Long, entity: RestOfPillEntity) =
        repository.addRestOfPill(userId, entity)

    fun updateRestOfPill(userId: Long, entity: RestOfPillEntity) =
        repository.updateRestOfPill(userId, entity)

    fun deleteRestOfPill(userId: Long, restOfPillId: String) =
        repository.deleteRestOfPill(userId, restOfPillId)
}