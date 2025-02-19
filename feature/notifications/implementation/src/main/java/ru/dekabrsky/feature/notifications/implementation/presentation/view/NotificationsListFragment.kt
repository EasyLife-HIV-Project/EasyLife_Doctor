package ru.dekabrsky.feature.notifications.implementation.presentation.view

import android.os.Bundle
import android.view.View
import main.utils.setBoolVisibility
import ru.dekabrsky.easylife.basic.fragments.BasicFragment
import ru.dekabrsky.easylife.basic.viewBinding.viewBinding
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.feature.notifications.implementation.R
import ru.dekabrsky.feature.notifications.implementation.databinding.FmtNotificationsBinding
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.NotificationsListAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.presenter.BaseNotificationListPresenter

abstract class NotificationsListFragment<T : BaseNotificationListPresenter<*>> : BasicFragment(),
    NotificationsListView {
    override val layoutRes = R.layout.fmt_notifications
    protected val binding by viewBinding(FmtNotificationsBinding::bind)

    abstract val isForChild: Boolean

    private val adapter by lazy {
        NotificationsListAdapter(
            onItemClick = presenter::onNotificationClick,
            onItemDelete = presenter::onNotificationDelete,
            onItemCheckedChanged = presenter::onItemCheckedChanged,
            formatDosage = presenter::formatDosage,
            isForChild = isForChild
        )
    }

    abstract var presenter: T

    override fun onResume() {
        super.onResume()
        setNavBarVisibility(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.basesCardsList.adapter = adapter
        binding.toolbar.setTitle(R.string.notifications)
        binding.addNotification.setOnClickListener { presenter.onAddNotificationClick() }
        binding.restOfPillsLayout.setOnClickListener { presenter.onRestOfPillsClick() }
    }
    override fun setChatsList(items: List<NotificationEntity>) {
        adapter.updateItems(items)
    }

    override fun setEmptyLayoutVisibility(isVisible: Boolean) {
        binding.emptyLayout.setBoolVisibility(isVisible)
    }

    fun setNavBarVisibility(isVisible: Boolean) {
        (parentFragment as NotificationFlowFragment).setNavBarVisibility(isVisible)
    }

    override fun setListLoadingVisibility(isVisible: Boolean) {
        binding.loadingText.setBoolVisibility(isVisible)
    }

    override fun setToolbarBackButton() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}