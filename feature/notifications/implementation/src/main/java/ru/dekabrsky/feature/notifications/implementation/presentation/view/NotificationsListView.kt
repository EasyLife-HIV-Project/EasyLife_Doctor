package ru.dekabrsky.feature.notifications.implementation.presentation.view

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.italks.basic.fragments.BasicView

@AddToEndSingle
interface NotificationsListView: BasicView {
    fun setEmptyLayoutVisibility(isVisible: Boolean)
    fun setChatsList(items: List<NotificationEntity>)
    fun setListLoadingVisibility(isVisible: Boolean)
    fun setToolbarBackButton()
}

@AddToEndSingle
interface DoctorNotificationsListView: NotificationsListView {
    fun setTitle(title: String)
}

interface ChildNotificationsListView: NotificationsListView