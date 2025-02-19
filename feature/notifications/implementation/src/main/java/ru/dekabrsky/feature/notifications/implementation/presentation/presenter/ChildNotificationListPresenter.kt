package ru.dekabrsky.feature.notifications.implementation.presentation.presenter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import main.utils.orZero
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.network.utils.ServerErrorHandler
import ru.dekabrsky.easylife.basic.resources.ResourceProvider
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.feature.loginCommon.presentation.model.LoginDataCache
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.feature.notifications.common.presentation.model.NotificationsFlowArgs
import ru.dekabrsky.feature.notifications.common.utils.NotificationToStringFormatter
import ru.dekabrsky.feature.notifications.implementation.R
import ru.dekabrsky.feature.notifications.implementation.domain.interactor.NotificationInteractor
import ru.dekabrsky.feature.notifications.implementation.presentation.model.RestOfPillsListScreenArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.view.ChildNotificationsListView
import ru.dekabrsky.feature.notifications.implementation.receiver.NotificationsReceiver
import ru.dekabrsky.sharedpreferences.SharedPreferencesProvider
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetMode
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetScreenArgs
import java.util.Calendar
import javax.inject.Inject

@Suppress("LongParameterList")
class ChildNotificationListPresenter @Inject constructor(
    private val router: FlowRouter,
    private val interactor: NotificationInteractor,
    private val context: Context,
    private val flowArgs: NotificationsFlowArgs,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val formatter: NotificationToStringFormatter,
    private val loginDataCache: LoginDataCache,
    private val resourceProvider: ResourceProvider,
    private val errorHandler: ServerErrorHandler
) : BaseNotificationListPresenter<ChildNotificationsListView>(
    router,
    interactor,
    flowArgs,
    formatter,
    errorHandler
) {

    private var notificationIds = sharedPreferencesProvider.notificationIds
    private var notificationIdsCache: MutableSet<Long> = mutableSetOf()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        notificationIds.get().forEach {
            cancelNotification(it)
        }
    }

    override fun dispatchNotifications(list: List<NotificationEntity>) {
        super.dispatchNotifications(list)

        list.filter { it.enabled }.forEach { notification ->
            addNotification(notification)
        }

        notificationIds.set(notificationIdsCache)

        checkHasUpdatesFromNotification()
    }

    private fun checkHasUpdatesFromNotification() {
        loginDataCache.medicinesDiff?.let { diff ->
            router.navigateTo(
                Flows.Common.SCREEN_BOTTOM_INFO,
                BottomSheetScreenArgs(
                    title = resourceProvider.getString(R.string.doctor_update_notifications),
                    subtitle = diff,
                    mode = BottomSheetMode.GAME,
                    icon = R.drawable.clock
                )
            )

            loginDataCache.medicinesDiff = null
        }
    }

    private fun addNotification(notificationEntity: NotificationEntity) {
        notificationIdsCache.add(notificationEntity.uid.orZero())

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val hour = notificationEntity.hour
        val minute = notificationEntity.minute

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        val notificationTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute))

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)

        if (LocalDateTime.now() > notificationTime) {
            calendar.set(Calendar.DAY_OF_YEAR, LocalDate.now().dayOfYear + 1)
        }

        val notifyIntent = Intent(context, NotificationsReceiver::class.java)

        notifyIntent.putExtras(
            Bundle().apply {
                putLong("NOTIFICATION_TIME", calendar.timeInMillis)
                putLong("NOTIFICATION_ID", notificationEntity.uid.orZero())
                putSerializable("NOTIFICATION", notificationEntity)
            }
        )

        val notifyPendingIntent = PendingIntent.getBroadcast(
            context,
            notificationEntity.uid?.toInt().orZero(),
            notifyIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        Log.d("addNotification: ", notificationEntity.uid?.toInt().orZero().toString())

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            notifyPendingIntent
        )

    }

    private fun cancelNotification(notificationUid: Long) {
        notificationIdsCache.remove(notificationUid)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val notificationIntent = Intent(context, NotificationsReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationUid.toInt().orZero(),
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    override fun dispatchNotificationDelete(notificationEntity: NotificationEntity) {
        cancelNotification(notificationEntity.uid.orZero())
        super.dispatchNotificationDelete(notificationEntity)
    }

    override fun onRestOfPillsClick() {
        loginDataCache.currentUserData?.id?.let { id ->
            router.navigateTo(
                Flows.Notifications.SCREEN_REST_OF_PILLS_LIST,
                RestOfPillsListScreenArgs(id)
            )
        }
    }
}