package ru.dekabrsky.easylife.basic.dateTime

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.util.Date
import java.util.Locale

fun formatDateTimeToUiDateTime(dateTime: LocalDateTime): String = formatDateTime(dateTime, "d MMM yyyy, HH:mm")

fun formatDateToUiDate(dateTime: LocalDateTime): String = formatDateTime(dateTime, "d MMM yyyy")
fun formatDateToUiDate(date: LocalDate): String = formatDateTime(date, "d MMM yyyy")

fun formatDateToUiDateShort(dateTime: LocalDate): String = formatDateTime(dateTime, "dd.MM.yyyy")

fun formatDateTimeToUiTime(dateTime: LocalDateTime): String = formatDateTime(dateTime, "HH:mm")

fun formatDateToServerString(date: LocalDate): String = formatDateTime(date.atTime(0, 0), SERVER_DATE_FORMAT)

fun formatHourAndMinute(hour: Int?, minute: Int?, withSeconds: Boolean = false) : String {
    if (hour == null || minute == null) return ""
    val hour = if (hour < MIN_TWO_DIGIT_MINUTE) "0$hour" else hour.toString()
    val minute = if (minute < MIN_TWO_DIGIT_MINUTE) "0$minute" else minute.toString()
    val second = "00".takeIf { withSeconds }
    return listOfNotNull(hour, minute, second).joinToString(":")
}

fun hourAndMinuteFromString(time: String?): Pair<Int, Int> {
    val parts = time?.split(":")
    val hour = parts?.firstOrNull()?.toIntOrNull() ?: 0
    val minute = parts?.getOrNull(1)?.toIntOrNull() ?: 0
    return hour to minute
}

private fun formatDateTime(dateTime: LocalDateTime, format: String): String =
    DateTimeFormatter.ofPattern(format)
        .withLocale(Locale("ru"))
        .format(dateTime)

private fun formatDateTime(dateTime: LocalDate, format: String): String =
    DateTimeFormatter.ofPattern(format)
        .withLocale(Locale("ru"))
        .format(dateTime)

fun LocalDate.toDate(): Date = Date(atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())

fun LocalDateTime.toDate(zoneOffset: ZoneOffset = ZoneOffset.UTC): Date =
    Date(toInstant(zoneOffset).toEpochMilli())

fun tryParseServerDate(date: String) = try {
    LocalDate.parse(date, DateTimeFormatter.ofPattern(SERVER_DATE_FORMAT))
} catch (e: DateTimeParseException) {
    null
}

fun tryParseDate(date: String, dateFormat: String) = try {
    LocalDate.parse(date, DateTimeFormatter.ofPattern(dateFormat))
} catch (e: DateTimeParseException) {
    null
}

fun tryParseServerDateTime(date: String) = try {
    LocalDateTime
        .parse(date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
        .atZone(ZoneOffset.UTC)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()
} catch (e: DateTimeParseException) {
    null
}

private const val MIN_TWO_DIGIT_MINUTE = 10
const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"