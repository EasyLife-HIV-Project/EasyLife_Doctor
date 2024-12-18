package ru.dekabrsky.easylife.game.view.presenter

import android.content.Context
import android.media.MediaPlayer
import ru.dekabrsky.analytics.AnalyticsSender
import ru.dekabrsky.analytics.AnalyticsUtils
import ru.dekabrsky.easylife.basic.navigation.router.FlowRouter
import ru.dekabrsky.easylife.basic.presenter.BasicPresenter
import ru.dekabrsky.easylife.flows.Flows
import ru.dekabrsky.easylife.game.R
import ru.dekabrsky.easylife.game.avatarCustomization.utils.getCatSettingsFromPrefs
import ru.dekabrsky.easylife.game.view.MainRoomView
import ru.dekabrsky.easylife.game.view.cache.GameFlowCache
import ru.dekabrsky.easylife.game.view.mapper.ItemsVisibilityMapper
import ru.dekabrsky.easylife.game.view.mapper.ShelfItemsUiMapper
import ru.dekabrsky.easylife.game.view.model.RoomColor
import ru.dekabrsky.easylife.scopes.Scopes
import ru.dekabrsky.feature.notifications.common.presentation.model.NotificationsFlowArgs
import ru.dekabrsky.sharedpreferences.SharedPreferencesProvider
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetMode
import ru.dekabrsky.simpleBottomsheet.view.model.BottomSheetScreenArgs
import ru.dekabrsky.simpleBottomsheet.view.model.ButtonState
import javax.inject.Inject

@Suppress("MagicNumber", "LongParameterList")
class MainRoomPresenter @Inject constructor(
    val router: FlowRouter,
    private val mapper: ShelfItemsUiMapper,
    private val visibilityMapper: ItemsVisibilityMapper,
    private val mediaPlayer: MediaPlayer,
    private val gameFlowCache: GameFlowCache,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val analyticsSender: AnalyticsSender
) : BasicPresenter<MainRoomView>(router) {

    private val score get() = gameFlowCache.experience?.score ?: 0
    private var level = getLevel()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        if (gameFlowCache.isFromNotification) router.navigateTo(Flows.Game.SCREEN_GARDEN)

        setNewLevel()
        viewState.setupAvatar(router)
        observeMusicState()
        updateRoomColor()
        viewState.scrollToAvatar()
        AnalyticsUtils.sendScreenOpen(this, analyticsSender)
    }

    override fun attachView(view: MainRoomView) {
        super.attachView(view)
        gameFlowCache.isMusicOnSubject.value?.let { updateMusicState(it) }
        viewState.setScore(score.toString())
        viewState.updateCat(getCatSettingsFromPrefs(sharedPreferencesProvider))
        updateLevel()
    }

    private fun observeMusicState() {
        gameFlowCache.isMusicOnSubject
            .subscribeOnIo()
            .subscribe(::updateMusicState)
            .addFullLifeCycle()
    }

    fun onDoorClick() = router.navigateTo(
        Flows.Common.SCREEN_BOTTOM_INFO,
        BottomSheetScreenArgs(
            title = "Хочешь выйти во двор?",
            subtitle = "Играй в мини-игры и получай за них очки прогресса\n" +
                    "По мере набора очков тебе будут открываться новые элементы интерьера!",
            mode = BottomSheetMode.GAME,
            icon = R.drawable.coin,
            buttonState = ButtonState("Вперед!") { router.navigateTo(Flows.Game.SCREEN_GARDEN) }
        )
    )
    private fun setNewLevel() {
        viewState.updateItemsVisibility(level, visibilityMapper.map(level))
        viewState.setShelfItems(mapper.map(level))
    }

    private fun updateLevel() {
        val oldLevel = level
        level = getLevel()
        if (level != oldLevel) {
            setNewLevel()
            showNewLevelDialog()
        }
    }

    private fun getLevel() = when {
        score < 500 -> 1
        score < 1500 -> 2
        score < 4000 -> 3
        else -> 4
    }

    private fun showNewLevelDialog() {
        router.navigateTo(
            Flows.Common.SCREEN_BOTTOM_INFO,
            BottomSheetScreenArgs(
                title = "Уровень повышен!",
                subtitle = "Интерьер обновлен до $level уровня",
                mode = BottomSheetMode.GAME,
                icon = R.drawable.cup
            )
        )
    }

    fun onSpeakerClick() {
        gameFlowCache.isMusicOnSubject.onNext(gameFlowCache.isMusicOnSubject.value?.not() ?: false)
    }

    private fun updateMusicState(isMusicOn: Boolean) {
        viewState.setMusicIsOn(isMusicOn)
        if (isMusicOn) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }

    private fun turnOffMusic() = gameFlowCache.isMusicOnSubject.onNext(false)

    private fun turnOnMusic() = gameFlowCache.isMusicOnSubject.onNext(true)

    fun onClockClick() {
        updateMusicState(false)
        router.startFlow(Flows.Notifications.name, NotificationsFlowArgs(Scopes.SCOPE_FLOW_GAME))
    }

    fun onColorsClick(context: Context) {
        val selectedVariant = sharedPreferencesProvider.wallColor.get()

        val variantsWithColors = RoomColor.values().map {
            Pair(RoomColor.getColoredRusName(it.rusName, context), it.res)
        }

        viewState.showColorsDialog(
            selectedVariantIndex = RoomColor.indexOfRusName(selectedVariant),
            variants = variantsWithColors
        )
    }

    fun onColorSelected(which: Int) {
        sharedPreferencesProvider.wallColor.set(RoomColor.getByIndex(which).rusName)
        updateRoomColor()
    }

    private fun updateRoomColor() {
        val selectedVariant = RoomColor.getByRusName(sharedPreferencesProvider.wallColor.get())
        viewState.setRoomColor(selectedVariant.res)
    }

    fun onAvatarClick() {
        router.navigateTo(Flows.Game.SCREEN_AVATAR_CUSTOMIZATION)
    }

    override fun onBackPressed() {
        updateMusicState(false)
        super.onBackPressed()
    }
}