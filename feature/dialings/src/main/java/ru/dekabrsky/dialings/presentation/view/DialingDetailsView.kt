package ru.dekabrsky.dialings.presentation.view

import moxy.viewstate.strategy.alias.AddToEndSingle
import org.threeten.bp.LocalDateTime
import ru.dekabrsky.dialings_common.presentation.model.DialingUiModel
import ru.dekabrsky.italks.basic.fragments.BasicView
import java.util.*

@AddToEndSingle
interface DialingDetailsView: BasicView {
    fun setMainData(model: DialingUiModel)
    fun setupProgress(progressInt: Int, progress: String, details: String)
    fun setupTime(startTime: String, canEdit: Boolean)
    fun setupPieChart()
    fun setupLineChart()
    fun showDatePicker()
    fun showTimePicker(date: Date)
    fun showNewStartDate(newValue: String)
    fun setRunNowVisibility(isVisible: Boolean)
    fun showRunNowDialog()
}