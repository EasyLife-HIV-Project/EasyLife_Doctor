package ru.dekabrsky.dialings.presentation.view

import androidx.fragment.app.Fragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.dekabrsky.common.presentation.model.EventsFlowScreenArgs
import ru.dekabrsky.dialings.R
import ru.dekabrsky.dialings.presentation.presenter.DialingsFlowPresenter
import ru.dekabrsky.easylife.basic.di.inject
import ru.dekabrsky.easylife.basic.di.module
import ru.dekabrsky.easylife.basic.fragments.BasicFlowFragment
import ru.dekabrsky.easylife.basic.navigation.FragmentFlowNavigator
import ru.dekabrsky.easylife.basic.navigation.di.moduleFlow
import ru.dekabrsky.easylife.basic.navigation.router.AppRouter
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.easylife.scopes.Scopes
import ru.dekabrsky.easylife.tabs.presentation.fragment.TabsFlowFragment
import toothpick.Toothpick
import javax.inject.Inject

class DialingsFlowFragment : BasicFlowFragment(), DialingsFlowView {

    override val layoutRes = R.layout.basic_fragment_flow

    override val containerId = R.id.flowContainer

    override val scopeName = Scopes.SCOPE_FLOW_DIALINGS

    private lateinit var args: EventsFlowScreenArgs

    override fun provideNavigator(router: AppRouter): FragmentFlowNavigator =
        object : FragmentFlowNavigator(this, router, containerId) {
            override fun createFragment(screenKey: String?, data: Any?): Fragment? =
                when (screenKey) {
                    Flows.Events.SCREEN_DIALINGS_LIST -> DialingsListFragment.newInstance()
                    Flows.Events.SCREEN_DIALING_DETAILS ->
                        DialingDetailsFragment.newInstance(data as Int)
                    else -> super.createFragment(screenKey, data)
                }
        }

    @Inject
    @InjectPresenter
    lateinit var presenter: DialingsFlowPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun injectDependencies() {
        Toothpick.openScopes(args.parentScope, scopeName)
            .module { bind(EventsFlowScreenArgs::class.java).toInstance(args) }
            .moduleFlow()
            .inject(this)
    }

    override fun onFinallyFinished() {
        super.onFinallyFinished()
        Toothpick.closeScope(scopeName)
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    fun setNavBarVisibility(isVisible: Boolean) {
        (parentFragment as TabsFlowFragment).setNavBarVisibility(isVisible)
    }

    companion object {
        fun newInstance(args: EventsFlowScreenArgs) = DialingsFlowFragment().apply {
            this.args = args
        }
    }
}