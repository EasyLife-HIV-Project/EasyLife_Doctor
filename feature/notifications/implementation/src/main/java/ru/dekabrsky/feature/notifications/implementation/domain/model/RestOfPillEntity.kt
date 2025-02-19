package ru.dekabrsky.feature.notifications.implementation.domain.model

import org.threeten.bp.LocalDate
import java.util.concurrent.CountDownLatch

class RestOfPillEntity(
    val id: String? = null,
    val name: String,
    val inDayCount: Int,
    val endDate: LocalDate
)