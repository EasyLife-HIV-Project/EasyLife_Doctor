package ru.dekabrsky.callersbase.presentation.view

import androidx.fragment.app.Fragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.dekabrsky.callersbase.R
import ru.dekabrsky.callersbase.presentation.model.ChatConversationScreenArgs
import ru.dekabrsky.callersbase.presentation.model.ChatFlowCache
import ru.dekabrsky.callersbase.presentation.presenter.ChatFlowPresenter
import ru.dekabrsky.common.presentation.model.ChatsFlowScreenArgs
import ru.dekabrsky.easylife.basic.di.inject
import ru.dekabrsky.easylife.basic.di.module
import ru.dekabrsky.easylife.basic.fragments.BasicFlowFragment
import ru.dekabrsky.easylife.basic.navigation.FragmentFlowNavigator
import ru.dekabrsky.easylife.basic.navigation.di.moduleFlow
import ru.dekabrsky.easylife.basic.navigation.router.AppRouter
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.easylife.scopes.Scopes.SCOPE_FLOW_CHATS
import ru.dekabrsky.easylife.tabs.presentation.fragment.TabsFlowFragment
import toothpick.Toothpick
import javax.inject.Inject

class ChatFlowFragment : BasicFlowFragment(), ChatFlowView {

    override val layoutRes = R.layout.basic_fragment_flow

    override val containerId = R.id.flowContainer

    override val scopeName = SCOPE_FLOW_CHATS

    private lateinit var args: ChatsFlowScreenArgs

    override fun provideNavigator(router: AppRouter): FragmentFlowNavigator =
        object : FragmentFlowNavigator(this, router, containerId) {
            override fun createFragment(screenKey: String?, data: Any?): Fragment? =
                when (screenKey) {
                    Flows.Chats.SCREEN_CHATS_LIST -> ChatsListFragment.newInstance()
                    Flows.Chats.SCREEN_CHAT_CONVERSATION ->
                        ChatConversationFragment.newInstance(data as ChatConversationScreenArgs)
                    Flows.Chats.SCREEN_CHAT_NEW_CONTACTS ->
                        ChatNewContactsFragment.newInstance()
                    else -> super.createFragment(screenKey, data)
                }
        }

    @Inject
    @InjectPresenter
    lateinit var presenter: ChatFlowPresenter

    @ProvidePresenter
    fun providePresenter() = presenter

    override fun injectDependencies() {
        Toothpick.openScopes(args.parentScope, scopeName)
            .module {
                bind(ChatsFlowScreenArgs::class.java).toInstance(args)
                bind(ChatFlowCache::class.java).singletonInScope()
            }
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
        fun newInstance(args: ChatsFlowScreenArgs) =
            ChatFlowFragment().apply { this.args = args }
    }
}