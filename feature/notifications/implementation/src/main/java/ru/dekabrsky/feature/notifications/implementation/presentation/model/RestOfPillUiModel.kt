package ru.dekabrsky.feature.notifications.implementation.presentation.model

class RestOfPillUiModel(
    val id: String,
    val name: String,
    val inDayCount: Int,
    val restCount: Int,
    val date: String
) {
    val areGone: Boolean get() = restCount == 0
}