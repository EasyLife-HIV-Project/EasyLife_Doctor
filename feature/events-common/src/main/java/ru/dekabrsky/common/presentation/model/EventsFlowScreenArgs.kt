package ru.dekabrsky.common.presentation.model

import java.io.Serializable

class EventsFlowScreenArgs(
    val parentScope: String,
    val screenKey: String,
    val dialingId: Int? = null
): Serializable