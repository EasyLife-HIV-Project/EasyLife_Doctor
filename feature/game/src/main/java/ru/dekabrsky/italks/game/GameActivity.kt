package ru.dekabrsky.easylife.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.dekabrsky.easylife.basic.di.inject
import ru.dekabrsky.easylife.basic.rx.RxSchedulers
import ru.dekabrsky.easylife.game.data.domain.interactor.GameInteractor
import ru.dekabrsky.easylife.game.view.cache.GameFlowCache
import ru.dekabrsky.easylife.scopes.Scopes
import toothpick.Toothpick
import javax.inject.Inject

class GameActivity: AndroidApplication(), FlappyBird.MyGameCallback {

    // нужен рефакторинг
    @Inject
    lateinit var gameInteractor: GameInteractor

    @Inject
    lateinit var cache: GameFlowCache

    private var progressIsLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flappy = FlappyBird()
        Toothpick.openScope(Scopes.SCOPE_FLOW_GAME).inject(this)
        flappy.setMyGameCallback(this)
        initialize(flappy, AndroidApplicationConfiguration())
    }

    override fun exitGame() {
        finish()
    }

    @SuppressLint("CheckResult")
    override fun onLost(score: Int) {
        if (progressIsLoaded) return
        progressIsLoaded = true
        val gameId = intent.getIntExtra("gameId", 0)
        val multipliedScore = score * SCORE_MULTIPLIER
        gameInteractor.postGameProgress(gameId, multipliedScore, true)
            .subscribeOn(RxSchedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                showToast(multipliedScore)
                cache.experience = it
            }, {})
    }

    private fun showToast(score: Int) {
        Toast.makeText(this, "Получено $score коинов", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val SCORE_MULTIPLIER = 5
    }
}