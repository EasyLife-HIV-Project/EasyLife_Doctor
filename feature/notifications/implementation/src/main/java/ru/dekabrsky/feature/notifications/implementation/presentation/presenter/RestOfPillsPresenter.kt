package ru.dekabrsky.feature.notifications.implementation.presentation.presenter

import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import ru.dekabrsky.analytics.AnalyticsSender
import ru.dekabrsky.analytics.AnalyticsUtils
import ru.dekabrsky.easylife.basic.dateTime.formatDateToUiDate
import ru.dekabrsky.easylife.basic.dateTime.formatDateToUiDateShort
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.network.utils.ServerErrorHandler
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.basic.rx.withCustomLoadingViewIf
import ru.dekabrsky.easylife.basic.rx.withLoadingView
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.feature.notifications.implementation.domain.interactor.RestOfPillInteractor
import ru.dekabrsky.feature.notifications.implementation.domain.model.RestOfPillEntity
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.RestOfPillAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.model.EditRestOfPillsScreenArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.model.RestOfPillUiModel
import ru.dekabrsky.feature.notifications.implementation.presentation.model.RestOfPillsListScreenArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.view.RestOfPillsView
import javax.inject.Inject
import kotlin.time.DurationUnit

class RestOfPillsPresenter @Inject constructor(
    private val router: FlowRouter,
    private val args: RestOfPillsListScreenArgs,
    private val interactor: RestOfPillInteractor,
    private val errorHandler: ServerErrorHandler,
    //private val analyticsSender: AnalyticsSender,
): BasicPresenter<RestOfPillsView>(router), RestOfPillAdapter.RestOfPillDataStore {

    private val now = LocalDate.now()
    private var entities: List<RestOfPillEntity> = listOf()
    override val items: List<RestOfPillUiModel> get() = entities.map(::mapRestOfPillToUi)

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        //AnalyticsUtils.sendScreenOpen(this, analyticsSender)
        loadList(withLoading = true)
    }

    override fun onItemDeleteClicked(itemId: String) =
        viewState.showDeleteDialog(itemId)

    override fun onItemEditClicked(itemId: String) {
        val entity = entities.find { it.id == itemId } ?: return
        listenEditResult()
        router.navigateTo(
            Flows.Notifications.SCREEN_EDIT_REST_OF_PILLS,
            EditRestOfPillsScreenArgs(args.patientId, entity)
        )
    }

    private fun loadList(withLoading: Boolean = false) {
        interactor.getRestOfPillList(args.patientId)
            .subscribeOnIo()
            .withCustomLoadingViewIf({ viewState.setLoadingListVisibility(it) }, withLoading)
            .subscribe({
                entities = it
                viewState.updateList()
            }, { errorHandler.onError(it, viewState) })
            .addFullLifeCycle()
    }

    private fun mapRestOfPillToUi(it: RestOfPillEntity): RestOfPillUiModel =
        RestOfPillUiModel(
            id = it.id.orEmpty(),
            name = it.name,
            inDayCount = it.inDayCount,
            restCount = maxOf(
                Duration.between(now.atStartOfDay(), it.endDate.atStartOfDay()).toDays().toInt()
                        * it.inDayCount,
                0
            ),
            date = formatDateToUiDateShort(it.endDate)
        )

    fun onAddPillClick() {
        listenEditResult()
        router.navigateTo(Flows.Notifications.SCREEN_EDIT_REST_OF_PILLS, EditRestOfPillsScreenArgs(args.patientId))
    }

    private fun listenEditResult() {
        router.setResultListener(EditRestOfPillsPresenter.EDIT_REST_OF_PILLS_RESULT_CODE) {
            router.removeResultListener(EditRestOfPillsPresenter.EDIT_REST_OF_PILLS_RESULT_CODE)
            loadList()
        }
    }

    fun onDeleteConfirmed(itemId: String) {
        interactor.deleteRestOfPill(args.patientId, itemId)
            .subscribeOnIo()
            .withLoadingView(viewState)
            .subscribe({ loadList() }, { errorHandler.onError(it, viewState) })
            .addFullLifeCycle()
    }
}