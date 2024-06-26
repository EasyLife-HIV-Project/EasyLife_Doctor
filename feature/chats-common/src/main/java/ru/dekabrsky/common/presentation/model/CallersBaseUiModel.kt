package ru.dekabrsky.common.presentation.model

import ru.dekabrsky.common.domain.model.CallersBaseEntity
import java.io.Serializable

class CallersBaseUiModel(
    val date: String,
    val count: String,
    val title: String,
    val variables: List<String>,
    val fullData: CallersBaseEntity
) : Serializable