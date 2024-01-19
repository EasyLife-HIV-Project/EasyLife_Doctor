package ru.dekabrsky.scenarios.presentation.view

import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.dekabrsky.italks.basic.fragments.BasicView

@AddToEndSingle
interface InvitePatientView: BasicView {
    fun setParentState(selected: Boolean, parentName: String, link: String)
}