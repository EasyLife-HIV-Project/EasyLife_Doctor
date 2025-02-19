package ru.dekabrsky.feature.notifications.implementation.presentation.model

import ru.dekabrsky.feature.notifications.implementation.domain.model.RestOfPillEntity
import java.io.Serializable

class EditRestOfPillsScreenArgs(
    val patientId: Long,
    val existingRestOfPill: RestOfPillEntity? = null
) : Serializable