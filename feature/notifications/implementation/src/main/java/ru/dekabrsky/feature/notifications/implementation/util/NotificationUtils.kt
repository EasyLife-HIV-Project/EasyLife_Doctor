package ru.dekabrsky.feature.notifications.implementation.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ru.dekabrsky.feature.notifications.api.R
import ru.dekabrsky.feature.notifications.implementation.provider.AppActivityProvider
import ru.dekabrsky.italks.scopes.Scopes
import toothpick.Toothpick

//TODO Проверить, работу нескольких уведомлений подряд с разными id
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val activityProvider = Toothpick.openScope(Scopes.SCOPE_APP_ROOT).getInstance(AppActivityProvider::class.java)

    val activityIntent = Intent(applicationContext, activityProvider.get()).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.easy_life_notification_channel_id)
    )

    builder.setSmallIcon(R.drawable.ic_logo)
        .setContentTitle(applicationContext.getString(R.string.easy_life_notification_header))
        .setContentText("Открыть приложение")
        .setContentIntent(
            PendingIntent.getActivity(
                applicationContext,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .setContentText(messageBody)

    notify(NOTIFICATION_ID, builder.build())
}