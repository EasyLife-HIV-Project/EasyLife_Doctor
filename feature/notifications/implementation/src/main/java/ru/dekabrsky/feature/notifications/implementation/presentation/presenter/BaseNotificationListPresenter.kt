package ru.dekabrsky.feature.notifications.implementation.presentation.presenter

import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.network.utils.ServerErrorHandler
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.basic.rx.withCustomLoadingViewIf
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.easylife.scopes.Scopes
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationMedicineEntity
import ru.dekabrsky.feature.notifications.common.presentation.model.NotificationsFlowArgs
import ru.dekabrsky.feature.notifications.common.utils.NotificationToStringFormatter
import ru.dekabrsky.feature.notifications.implementation.domain.interactor.INotificationInteractor
import ru.dekabrsky.feature.notifications.implementation.presentation.view.NotificationsListView

abstract class BaseNotificationListPresenter<T: NotificationsListView>(
    private val router: FlowRouter,
    private val interactor: INotificationInteractor,
    private val flowArgs: NotificationsFlowArgs,
    private val formatter: NotificationToStringFormatter,
    private val errorHandler: ServerErrorHandler
) : BasicPresenter<T>(router) {

    private var isFirstLoading = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (flowArgs.parentScopeName != Scopes.SCOPE_APP) {
            viewState.setToolbarBackButton()
        }
    }

    override fun attachView(view: T) {
        super.attachView(view)
        getAll()
    }

    private fun getAll() {
        interactor.getAll()
            .subscribeOnIo()
            .withCustomLoadingViewIf(viewState::setListLoadingVisibility, isFirstLoading)
            .subscribe(::dispatchNotifications) { errorHandler.onError(it, viewState) }
            .addFullLifeCycle()
    }

    protected open fun dispatchNotifications(list: List<NotificationEntity>) {
        isFirstLoading = false
        viewState.setChatsList(list)
        viewState.setEmptyLayoutVisibility(list.isEmpty())
    }

    fun onAddNotificationClick() {
        router.navigateTo(Flows.Notifications.SCREEN_EDIT_NOTIFICATION)
    }

    fun onNotificationDelete(notificationEntity: NotificationEntity) {
        interactor.delete(notificationEntity)
            .subscribeOnIo()
            .subscribe(
                { dispatchNotificationDelete(notificationEntity) },
                { errorHandler.onError(it, viewState) }
            )
            .addFullLifeCycle()
    }

    protected open fun dispatchNotificationDelete(notificationEntity: NotificationEntity) {
        getAll()
    }

    fun onNotificationClick(notificationEntity: NotificationEntity) {
        router.navigateTo(Flows.Notifications.SCREEN_EDIT_NOTIFICATION, notificationEntity)
    }

    fun onItemCheckedChanged(notificationEntity: NotificationEntity, isEnabled: Boolean) {
        interactor.update(notificationEntity.copy(enabled = isEnabled))
            .subscribeOnIo()
            .subscribe({ getAll() }, { errorHandler.onError(it, viewState) })
            .addFullLifeCycle()
    }

    fun formatDosage(medicineEntity: NotificationMedicineEntity) =
        formatter.formatDosage(medicineEntity)

    abstract fun onRestOfPillsClick()
}