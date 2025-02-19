package ru.dekabrsky.feature.notifications.implementation.presentation.view

import android.os.Bundle
import android.view.View
import main.utils.onTextChange
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.dekabrsky.easylife.basic.di.module
import ru.dekabrsky.easylife.basic.fragments.BasicFragment
import ru.dekabrsky.easylife.basic.viewBinding.viewBinding
import ru.dekabrsky.feature.notifications.common.domain.model.NotificationEntity
import ru.dekabrsky.feature.notifications.implementation.R
import ru.dekabrsky.feature.notifications.implementation.databinding.FmtEditRestOfPillBinding
import ru.dekabrsky.feature.notifications.implementation.databinding.FmtRestOfPillsListBinding
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.MedicineAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.adapter.RestOfPillAdapter
import ru.dekabrsky.feature.notifications.implementation.presentation.model.EditRestOfPillsScreenArgs
import ru.dekabrsky.feature.notifications.implementation.presentation.presenter.EditRestOfPillsPresenter
import ru.dekabrsky.feature.notifications.implementation.presentation.presenter.NotificationEditPresenter
import ru.dekabrsky.feature.notifications.implementation.presentation.presenter.RestOfPillsPresenter
import toothpick.Toothpick

class EditRestOfPillsFragment(
    private val notificationsScope: String,
    private var args: EditRestOfPillsScreenArgs
): BasicFragment(), EditRestOfPillsView {

    override val layoutRes = R.layout.fmt_edit_rest_of_pill

    private val binding by viewBinding(FmtEditRestOfPillBinding::bind)

    @InjectPresenter
    lateinit var presenter: EditRestOfPillsPresenter

    @ProvidePresenter
    fun providePresenter(): EditRestOfPillsPresenter {
        return Toothpick.openScopes(notificationsScope, scopeName)
            .module { bind(EditRestOfPillsScreenArgs::class.java).toInstance(args) }
            .getInstance(EditRestOfPillsPresenter::class.java)
            .also { Toothpick.closeScope(scopeName) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
        binding.toolbar.setNavigationOnClickListener { presenter.onBackPressed() }
        binding.toolbar.setTitle(R.string.edit_pill)

        binding.name.onTextChange { presenter.onNameChanged(it) }
        binding.inDayCount.onTextChange { presenter.onInDayCountChanged(it) }
        binding.restCount.onTextChange { presenter.onRestCountChanged(it) }
        binding.doneBtn.setOnClickListener { presenter.onDoneClicked() }
    }

    override fun setName(nameString: String) =
        binding.name.setText(nameString)

    override fun setInDayCount(inDayCount: String) =
        binding.inDayCount.setText(inDayCount)

    override fun setRestCount(restCount: String) =
        binding.restCount.setText(restCount)

    override fun setEndDate(date: String) {
        binding.predictedDate.text = getString(R.string.predicted_date_pattern, date)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    companion object {
        fun newInstance(
            notificationsScope: String,
            args: EditRestOfPillsScreenArgs
        ) = EditRestOfPillsFragment(notificationsScope, args)
    }
}