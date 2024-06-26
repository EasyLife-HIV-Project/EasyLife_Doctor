package ru.dekabrsky.feature.notifications.implementation.data.repository

import main.utils.orZero
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.feature.notifications.implementation.data.api.NotificationApi
import ru.dekabrsky.feature.notifications.implementation.data.mapper.NotificationResponseToEntityMapper
import javax.inject.Inject

class NotificationApiRepository @Inject constructor(
    private val api: NotificationApi,
//    private val dao: NotificationDao,
    private val mapper: NotificationResponseToEntityMapper,
//    private val dbMapper: NotificationDbToEntityMapper,
//    private val dbRepository: NotificationRepository
) {

    fun getAll() = api.getNotifications().map { list -> list.sortedBy { it.time }.map { mapper.mapResponseToEntity(it) } }
//        Single.error<List<NotificationEntity>>(
//        HttpException(
//            Response.error<List<NotificationEntity>>(
//                401,
//                ResponseBody.create("application/json".toMediaType(), "null")
//            )
//        )
//    )
//    На случай, если нужно будет сохранять в бд
//    fun getAll() =
//        api
//            .getNotifications()
//            .map { list -> list.map { mapper.mapResponseToEntity(it) } }
//            .flatMapCompletable { entities ->
//                dao.deleteAll()
//                    .andThen(dao.insertAll(entities.map { dbMapper.entityToDb(it) }) )
//            }
//            .andThen(dbRepository.getAll())

    fun add(notification: NotificationEntity) =
        api
            .postNotification(mapper.mapEntityToRequest(notification))

    fun delete(notificationId: Long) = api.deleteNotification(notificationId)

    fun update(notification: NotificationEntity) =
        api.putNotification(notification.uid.orZero(), mapper.mapEntityToRequest(notification))

}