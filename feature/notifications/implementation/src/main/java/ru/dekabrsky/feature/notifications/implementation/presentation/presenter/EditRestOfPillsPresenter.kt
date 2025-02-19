package ru.dekabrsky.feature.notifications.implementation.presentation.presenter

import main.utils.orZero
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import ru.dekabrsky.analytics.AnalyticsSender
import ru.dekabrsky.analytics.AnalyticsUtils
import ru.dekabrsky.easylife.basic.dateTime.formatDateToUiDate
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.basic.rx.withLoadingView
import ru.dekabrsky.feature.notifications.implementation.domain.interactor.RestOfPillInteractor
import ru.dekabrsky.feature.notifications.implementation.domain.model.RestOfPillEntity
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.RestOfPillAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.model.EditRestOfPillsScreenArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.model.RestOfPillUiModel
import ru.dekabrsky.feature.notifications.implementation.presentation.view.EditRestOfPillsView
import ru.dekabrsky.feature.notifications.implementation.presentation.view.RestOfPillsView
import javax.inject.Inject

class EditRestOfPillsPresenter @Inject constructor(
    private val router: FlowRouter,
    private val interactor: RestOfPillInteractor,
    private val args: EditRestOfPillsScreenArgs,
    private val analyticsSender: AnalyticsSender,
): BasicPresenter<EditRestOfPillsView>(router) {

    private val now = LocalDate.now()

    private var name: String = ""
    private var inDayCount: String = ""
    private val inDayCountNumber: Int get() = inDayCount.toIntOrNull().orZero()
    private var restCount: String = ""
    private val restCountNumber: Int get() = restCount.toIntOrNull().orZero()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        AnalyticsUtils.sendScreenOpen(this, analyticsSender)
        args.existingRestOfPill?.let {
            viewState.setName(it.name)
            viewState.setInDayCount(it.inDayCount.toString())
            viewState.setRestCount(
                maxOf(Duration.between(now.atStartOfDay(), it.endDate.atStartOfDay()).toDays().toInt()
                        * it.inDayCount, 0).toString()
            )
        }
    }

    fun onNameChanged(name: String) {
        this.name = name
    }

    fun onInDayCountChanged(inDayCount: String) {
        this.inDayCount = inDayCount
        updateEndDate()
    }

    fun onRestCountChanged(restCount: String) {
        this.restCount = restCount
        updateEndDate()
    }

    fun onDoneClicked() {
        if (
            name.isEmpty()
            || inDayCountNumber <= 0
            || restCount.toIntOrNull() == null
            || restCount.toInt() < 0
        ) {
            viewState.showToast("Поля заполнены некорректно")
            return
        }

        val restOfPill = RestOfPillEntity(
            id = args.existingRestOfPill?.id,
            name = name,
            inDayCount = inDayCountNumber,
            endDate = calculateEndDate()
        )

        if (args.existingRestOfPill == null) {
            interactor.addRestOfPill(args.patientId, restOfPill)
        } else {
            interactor.updateRestOfPill(args.patientId, restOfPill)
        }
            .subscribeOnIo()
            .withLoadingView(viewState)
            .subscribe { router.backWithResult(EDIT_REST_OF_PILLS_RESULT_CODE) }
            .addFullLifeCycle()
    }

    private fun calculateEndDate() =
        now.plusDays(restCountNumber.toLong() / inDayCountNumber)

    private fun updateEndDate() {
        if (inDayCountNumber <= 0 || restCountNumber <= 0) return

        viewState.setEndDate(formatDateToUiDate(calculateEndDate()))
    }

    companion object {
        val EDIT_REST_OF_PILLS_RESULT_CODE = this::class.hashCode()
    }
}