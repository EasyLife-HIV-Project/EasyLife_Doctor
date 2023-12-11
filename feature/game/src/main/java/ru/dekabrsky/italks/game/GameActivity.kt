package ru.dekabrsky.italks.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import ru.dekabrsky.italks.game.data.model.Progress
import ru.dekabrsky.italks.game.data.model.ProgressDb

@Suppress("MagicNumber")
class GameActivity: AndroidApplication(), FlappyBird.MyGameCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val flappy = FlappyBird()
        flappy.setMyGameCallback(this)
        initialize(flappy, AndroidApplicationConfiguration())
    }

    override fun exitGame() {
        finish()
    }

    override fun onPause() {
        super.onPause()
        val db = ProgressDb.getDb(this)
        val item = Progress(null,
            "Flappy",
            40
        )
        Thread{
            db.getDao().insertProgress(item)
        }.start()
    }
}