package ru.dekabrsky.callersbase.presentation.presenter

import ru.dekabrsky.callersbase.domain.interactor.ContactsInteractorImpl
import ru.dekabrsky.callersbase.presentation.model.ChatUiModel
import ru.dekabrsky.callersbase.presentation.view.BaseChatsListView
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.network.utils.ServerErrorHandler
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.basic.rx.withLoadingView

abstract class BaseChatListPresenter<T: BaseChatsListView> constructor(
    private val router: FlowRouter,
    private val interactor: ContactsInteractorImpl,
    private val errorHandler: ServerErrorHandler
) : BasicPresenter<T>(router) {

    protected var isFirstLoading = true

    override fun attachView(view: T) {
        super.attachView(view)
        load()
    }

    abstract fun load()

    protected open fun dispatchLoading(items: List<ChatUiModel>) {
        isFirstLoading = false
        viewState.showEmptyLayout(items.isEmpty())
        if (items.isEmpty().not()) {
            viewState.setChatsList(items)
        }
    }

    fun onChatClick(model: ChatUiModel) {
        if (model.chatIsStarted) {
            navigateToChat(model)
        } else {
            interactor.startChat(model.secondUser.id)
                .subscribeOnIo()
                .withLoadingView(viewState)
                .subscribe({ navigateToChat(model) }, { errorHandler.onError(it, viewState) })
                .addFullLifeCycle()
        }
    }

    abstract fun navigateToChat(model: ChatUiModel)
}