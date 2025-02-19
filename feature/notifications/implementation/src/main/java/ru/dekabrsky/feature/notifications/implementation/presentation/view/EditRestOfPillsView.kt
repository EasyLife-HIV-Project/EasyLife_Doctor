package ru.dekabrsky.feature.notifications.implementation.presentation.view

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.dekabrsky.easylife.basic.fragments.BasicView

@AddToEndSingle
interface EditRestOfPillsView: BasicView {
    fun setName(nameString: String)
    fun setInDayCount(inDayCount: String)
    fun setRestCount(restCount: String)
    fun setEndDate(date: String)
}