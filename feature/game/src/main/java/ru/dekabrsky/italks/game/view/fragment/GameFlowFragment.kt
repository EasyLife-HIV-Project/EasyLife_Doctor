package ru.dekabrsky.italks.game.view.fragment

import androidx.fragment.app.Fragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.dekabrsky.italks.basic.di.inject
import ru.dekabrsky.italks.basic.di.module
import ru.dekabrsky.italks.basic.fragments.BasicFlowFragment
import ru.dekabrsky.italks.basic.navigation.FlowFragmentProvider
import ru.dekabrsky.italks.basic.navigation.FragmentFlowNavigator
import ru.dekabrsky.italks.basic.navigation.di.moduleFlow
import ru.dekabrsky.italks.basic.navigation.router.AppRouter
import ru.dekabrsky.italks.flows.Flows
import ru.dekabrsky.italks.game.R
import ru.dekabrsky.italks.game.avatarCustomization.view.fragment.AvatarCustomizationFragment
import ru.dekabrsky.italks.game.view.GameFlowView
import ru.dekabrsky.italks.game.view.cache.GameFlowCache
import ru.dekabrsky.italks.game.view.presenter.GameFlowPresenter
import ru.dekabrsky.italks.scopes.Scopes
import ru.dekabrsky.italks.tabs.presentation.fragment.TabsFlowFragment
import ru.dekabrsky.simpleBottomsheet.view.fragment.SimpleInfoBottomSheet
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetScreenArgs
import toothpick.Toothpick
import javax.inject.Inject


class GameFlowFragment : BasicFlowFragment(), GameFlowView {

    override val layoutRes = R.layout.basic_fragment_flow

    override val containerId = R.id.flowContainer

    @Inject
    lateinit var flowFragmentProvider: FlowFragmentProvider

    override fun provideNavigator(router: AppRouter): FragmentFlowNavigator =
        object : FragmentFlowNavigator(this, router, containerId) {
            override fun createFragment(screenKey: String?, data: Any?): Fragment? =
                when (screenKey) {
                    Flows.Game.SCREEN_START_GAME -> StartGameFragment.newInstance()
                    Flows.Game.SCREEN_MAIN_ROOM -> MainRoomFragment.newInstance()
                    Flows.Game.SCREEN_GARDEN -> GardenFragment.newInstance()
                    Flows.Game.SCREEN_FIFTEEN -> FifteenFragment.newInstance()
                    Flows.Game.SCREEN_LEAVES -> LeavesFragment.newInstance()
                    Flows.Game.SCREEN_FOOTBALL -> FootballFragment.newInstance()
                    Flows.Game.SCREEN_AVATAR_CUSTOMIZATION -> AvatarCustomizationFragment.newInstance()
                    Flows.Common.SCREEN_BOTTOM_INFO -> SimpleInfoBottomSheet.newInstance(data as BottomSheetScreenArgs)
                    else -> super.createFragment(screenKey, data)
                }
        }

    override val scopeName = Scopes.SCOPE_FLOW_GAME

    @Inject
    @InjectPresenter
    lateinit var presenter: GameFlowPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun injectDependencies() {
        Toothpick.openScopes(Scopes.SCOPE_APP, scopeName)
            .module { bind(GameFlowCache::class.java).singletonInScope() }
            .moduleFlow()
            .inject(this)
    }

    override fun onFinallyFinished() {
        super.onFinallyFinished()
        Toothpick.closeScope(scopeName)
    }

    fun setNavBarVisibility(isVisible: Boolean) {
        (parentFragment as TabsFlowFragment).setNavBarVisibility(isVisible)
    }

    companion object {
        fun newInstance() = GameFlowFragment()
    }
}