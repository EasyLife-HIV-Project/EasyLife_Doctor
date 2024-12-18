package ru.dekabrsky.easylife.game.view.cache

import io.reactivex.subjects.BehaviorSubject
import ru.dekabrsky.easylife.game.data.domain.model.GameConfigEntity
import ru.dekabrsky.easylife.game.data.domain.model.GameProgressEntity
import javax.inject.Inject

class GameFlowCache @Inject constructor() {
    var experience: GameProgressEntity? = null
    var configs: List<GameConfigEntity> = listOf()
    val isMusicOnSubject : BehaviorSubject<Boolean> = BehaviorSubject.createDefault(true)
    var isFromNotification: Boolean = false
}