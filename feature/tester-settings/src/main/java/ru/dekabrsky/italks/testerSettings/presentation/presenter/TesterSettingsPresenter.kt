package ru.dekabrsky.easylife.testerSettings.presentation.presenter

import ru.dekabrsky.easylife.basic.di.ServerEndpoint
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.testerSettings.presentation.view.TesterSettingsView
import ru.dekabrsky.sharedpreferences.SharedPreferencesProvider
import javax.inject.Inject

class TesterSettingsPresenter @Inject constructor(
    private val router: FlowRouter,
    @ServerEndpoint val baseEndpoint: String,
    private val sharedPreferencesProvider: SharedPreferencesProvider
): BasicPresenter<TesterSettingsView>(router) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setupServerAddress(baseEndpoint)
    }

    fun onSaveBtnClicked(newAddress: String) {
        sharedPreferencesProvider.testUrl.set(newAddress)
        restartApp()
    }

    private fun restartApp() {
        //router.finishChain()
        //router.navigateToStart()
        viewState.restartApp()
    }

    fun setSavedUrl(text: CharSequence?) {
        if (text !is String) return
        viewState.setupServerAddress(text)
    }
}