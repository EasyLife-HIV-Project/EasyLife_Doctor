package ru.dekabrsky.feature.notifications.implementation.presentation.view

import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import ru.dekabrsky.easylife.basic.fragments.BasicView

@AddToEndSingle
interface RestOfPillsView: BasicView {
    fun updateList()
    fun setLoadingListVisibility(isLoading: Boolean)
    @OneExecution
    fun showDeleteDialog(itemId: String)
}