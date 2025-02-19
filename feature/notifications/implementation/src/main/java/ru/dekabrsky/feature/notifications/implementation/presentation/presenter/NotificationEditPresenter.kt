package ru.dekabrsky.feature.notifications.implementation.presentation.presenter

import main.utils.isTrue
import main.utils.orZero
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import ru.dekabrsky.analytics.AnalyticsSender
import ru.dekabrsky.analytics.AnalyticsUtils
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.basic.rx.withLoadingView
import ru.dekabrsky.feature.notifications.common.domain.model.DosageUnit
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.feature.notifications.common.presentation.model.NotificationsFlowArgs
import ru.dekabrsky.feature.notifications.implementation.domain.interactor.DoctorNotificationInteractor
import ru.dekabrsky.feature.notifications.implementation.domain.interactor.NotificationInteractor
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.MedicineAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.mapper.NotificationEntityToUiMapper
import ru.dekabrsky.feature.notifications.implementation.presentation.model.MedicineItemUiModel
import ru.dekabrsky.feature.notifications.implementation.presentation.model.NotificationEditUiModel
import ru.dekabrsky.feature.notifications.implementation.presentation.view.NotificationEditView
import java.util.Locale
import javax.inject.Inject

@Suppress("LongParameterList")
class NotificationEditPresenter @Inject constructor(
    router: FlowRouter,
    private val flowArgs: NotificationsFlowArgs,
    private val childInteractor: NotificationInteractor,
    private val doctorInteractor: DoctorNotificationInteractor,
    private val existingNotification: NotificationEntity,
    private val mapper: NotificationEntityToUiMapper,
    private val analyticsSender: AnalyticsSender
) : BasicPresenter<NotificationEditView>(router), MedicineAdapter.MedicineDataStore {

    private val interactor = flowArgs.patientId?.let { id ->
        doctorInteractor.apply { setUserId(id) }
    } ?: childInteractor

    private var notification = existingNotification.uid?.let {
        mapper.mapEntityToUi(existingNotification)
    } ?: NotificationEditUiModel()

    private val daysInWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        AnalyticsUtils.sendScreenOpen(this, analyticsSender)
        notification.let {
            viewState.updateMedicines()
            it.hour?.let { hour -> it.minute?.let { minute -> onTimeSet(hour, minute) } }
            updateSelectedDays()
            if (it.withDuration) {
                viewState.setDurationFieldsVisibility(true)
                viewState.setDurationSwitchIsChecked(true)
                updateDurationFieldsByModel()
            }
            viewState.setNotificationEnabled(it.enabled)
        }

    }

    fun onTimeClick() {
        viewState.showTimePicker(notification.hour.orZero(), notification.minute.orZero())
    }

    fun onDoneClick() {
        if (validate().not()) return

        val result = mapper.mapUiToEntity(notification, existingNotification.uid)

        if (existingNotification.uid == null) {
            interactor.add(result)
                .subscribeOnIo()
                .withLoadingView(viewState)
                .subscribe({
                    onBackPressed()
                }, { viewState.showError(it) })
                .addFullLifeCycle()
        } else {
            interactor.update(result)
                .subscribeOnIo()
                .withLoadingView(viewState)
                .subscribe({
                    onBackPressed()
                }, { viewState.showError(it) })
                .addFullLifeCycle()
        }
    }

    private fun validate(): Boolean {
        val errors = mutableListOf<String>().apply {
            if (notification.medicines.any { it.name.isEmpty() }) add("Введите название лекарства")
            if (notification.hour == null) add("Выберите время")
            if (notification.selectedDays.isEmpty()) add("Выберите минимум один день недели")
            if (notification.withDuration && (notification.endDate == null || notification.startDate == null)) {
                add("Укажите длительность курса")
            }
            if (notification.endDate?.isBefore(notification.startDate).isTrue()) add("Время начала позже времени конца")
        }

        if (errors.isNotEmpty()) {
            viewState.showToast(errors.joinToString("\n"))
            return false
        }
        return true
    }

    fun onTimeSet(hourOfDay: Int, minute: Int) {
        notification.hour = hourOfDay
        notification.minute = minute
        viewState.setTime(notification.time)
    }

    fun onStartDateSet(date: LocalDate) {
        notification.startDate = date
        viewState.setStartDate(notification.startDateString)
    }

    fun onEndDateSet(date: LocalDate) {
        notification.endDate = date
        viewState.setEndDate(notification.endDateString)
    }

    fun onStartDateClick() =
        viewState.showStartDatePicker(notification.startDate ?: LocalDate.now())

    fun onEndDateClick() =
        viewState.showEndDatePicker(notification.endDate ?: LocalDate.now())

    fun onDurationCheckedChanged(checked: Boolean) {
        notification.withDuration = checked
        viewState.setDurationFieldsVisibility(checked)
        notification.startDate = null
        notification.endDate = null
        updateDurationFieldsByModel()
    }

    private fun updateDurationFieldsByModel() {
        viewState.setStartDate(notification.startDateString)
        viewState.setEndDate(notification.endDateString)
    }

    fun onEnabledCheckedChanged(checked: Boolean) {
        notification.enabled = checked
    }

    override var items: MutableList<MedicineItemUiModel>
        get() = notification.medicines
        set(value) {
            notification.medicines = value
        }

    override fun onNameChanged(text: String, position: Int) {
        items[position].name = text
    }

    override fun onDosageChanged(text: String, position: Int) {
        items[position].dosage = text
    }

    override fun onNoteChanged(text: String, position: Int) {
        items[position].note = text
    }

    override fun onUnitChanged(unit: DosageUnit, position: Int) {
        items[position].unit = unit
    }

    fun onAddMedicineClicked() {
        items.add(MedicineItemUiModel())
        viewState.updateMedicines()
    }

    override fun onDelete(position: Int) {
        items.removeAt(position)
        viewState.updateMedicines()
    }

    fun onDaysSelected(result: MutableList<Boolean>) {
        notification.selectedDays =
            daysInWeek
                .mapIndexed { index, item -> index to item }
                .filter { (index, _) -> result[index] }
                .map { (_, item) -> item }
                .toList()
        updateSelectedDays()
    }

    fun onWeekDaysLayoutClicked() {
        viewState.showDaysDialog(
            selectedVariantIndices = daysInWeek.map { it in notification.selectedDays }
                .toBooleanArray(),
            variants = daysInWeek.map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) }
                .toTypedArray()
        )
    }

    private fun updateSelectedDays() {
        viewState.showSelectedDays(
            notification.selectedDays.joinToString {
                it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }
        )
    }
}