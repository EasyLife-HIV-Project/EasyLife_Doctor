package ru.dekabrsky.feature.notifications.implementation.presentation.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.dekabrsky.easylife.basic.di.module
import ru.dekabrsky.easylife.basic.fragments.BasicFragment
import ru.dekabrsky.easylife.basic.viewBinding.viewBinding
import ru.dekabrsky.feature.notifications.implementation.R
import ru.dekabrsky.feature.notifications.implementation.databinding.FmtRestOfPillsListBinding
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.RestOfPillAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.model.RestOfPillsListScreenArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.presenter.RestOfPillsPresenter
import toothpick.Toothpick

class RestOfPillsFragment(
    private val notificationsScope: String,
    private val args: RestOfPillsListScreenArgs
): BasicFragment(), RestOfPillsView {

    override val layoutRes = R.layout.fmt_rest_of_pills_list

    private val binding by viewBinding(FmtRestOfPillsListBinding::bind)

    @InjectPresenter
    lateinit var presenter: RestOfPillsPresenter

    private val adapter by lazy { RestOfPillAdapter(presenter) }

    @ProvidePresenter
    fun providePresenter(): RestOfPillsPresenter {
        return Toothpick.openScopes(notificationsScope, scopeName)
            .module { bind(RestOfPillsListScreenArgs::class.java).toInstance(args) }
            .getInstance(RestOfPillsPresenter::class.java)
            .also { Toothpick.closeScope(scopeName) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
        binding.toolbar.setTitle(R.string.rest_of_pills)

        (parentFragment as NotificationFlowFragment).setNavBarVisibility(false)

        binding.addPill.setOnClickListener { presenter.onAddPillClick() }

        binding.pillsList.adapter = adapter
        binding.pillsList.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun updateList() = adapter.notifyDataSetChanged()

    override fun setLoadingListVisibility(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.pillsList.isVisible = isLoading.not()
    }

    override fun showDeleteDialog(itemId: String) {
        AlertDialog.Builder(requireContext())
            .setMessage("Вы действительно хотите удалить элемент?")
            .setPositiveButton("Да") { _, _ -> presenter.onDeleteConfirmed(itemId) }
            .setNegativeButton("Отменить", null)
            .show()
    }

    companion object {

        fun newInstance(
            notificationsScope: String,
            args: RestOfPillsListScreenArgs
        ) = RestOfPillsFragment(notificationsScope, args)
    }
}