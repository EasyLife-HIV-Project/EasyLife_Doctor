package ru.dekabrsky.italks.profile.view.presenter

import ru.dekabrsky.feature.loginCommon.domain.interactor.LoginInteractor
import ru.dekabrsky.feature.loginCommon.presentation.model.LoginDataCache
import ru.dekabrsky.feature.loginCommon.presentation.model.TokenCache
import ru.dekabrsky.italks.basic.navigation.router.FlowRouter
import ru.dekabrsky.italks.basic.network.utils.ServerErrorHandler
import ru.dekabrsky.italks.basic.presenter.BasicPresenter
import ru.dekabrsky.italks.basic.rx.withLoadingView
import ru.dekabrsky.italks.flows.Flows
import ru.dekabrsky.italks.profile.R
import ru.dekabrsky.italks.profile.domain.interactor.ProfileInteractor
import ru.dekabrsky.italks.profile.domain.model.CodeEntity
import ru.dekabrsky.italks.profile.view.ProfileView
import ru.dekabrsky.sharedpreferences.SharedPreferencesProvider
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetMode
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetScreenArgs
import ru.dekabrsky.simpleBottomsheet.view.model.ButtonState
import javax.inject.Inject

class ProfilePresenter @Inject constructor(
    val router: FlowRouter,
    private val interactor: ProfileInteractor,
    private val loginInteractor: LoginInteractor,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val loginDataCache: LoginDataCache,
    private val tokenCache: TokenCache,
    private val errorHandler: ServerErrorHandler
) : BasicPresenter<ProfileView>(router) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        checkAvatars()
        viewState.setupAvatars(router)
        loginDataCache.currentUserData?.let {
            viewState.setTitle(it.nameWithNickname)
        }
    }

    fun generateParent() {
        interactor.generateParentCode()
            .subscribeOnIo()
            .subscribe(
                { dispatchParentCode(it) },
                { }
            )
            .addFullLifeCycle()
    }

    private fun checkAvatars() {
        if (sharedPreferencesProvider.gameAvatar.get().isEmpty()) {
            router.startFlow(Flows.Avatar.name)
        }
    }

    private fun dispatchParentCode(entity: CodeEntity) {
        viewState.setParentCode(entity.code)
    }

    fun onLogoutClick() {
        router.navigateTo(
            Flows.Common.SCREEN_BOTTOM_INFO,
            BottomSheetScreenArgs(
                title = "Выйти из учетной записи?",
                subtitle = "Придется снова ввести пароль",
                mode = BottomSheetMode.GAME,
                icon = R.drawable.bye,
                buttonState = ButtonState(text = "Да, выйти", action = ::makeLogout)
            )
        )
    }

    private fun makeLogout() {
        //tokenCache.accessToken = null
        loginInteractor.getFcmToken()
            .flatMapCompletable { token -> loginInteractor.logout(token) }
            .subscribeOnIo()
            .withLoadingView(viewState)
            .subscribe({ router.newRootFlow(Flows.Login.name) }, { errorHandler.onError(it, viewState) })
            .addFullLifeCycle()
    }
}