package world.inetumrealdolmen.mobiletrm.ui.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalTime
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Formats a duration in minutes into a human-readable string.
 *
 * @param minutes The total duration in minutes.
 * @return A formatted string representing the duration (e.g., "3d 5h 30min").
 */
fun formatTime(minutes: Long): String { // TODO maybe merge this with other formattime below
    val days = minutes / (60 * 24)
    val hours = (minutes % (60 * 24)) / 60
    val remainingMinutes = minutes % 60
    return "${days}d ${hours}h ${remainingMinutes}min"
}

/**
 * Formats a [LocalTime] to a human-readable string.
 * Defaults to [FormatStyle.SHORT] if no style is given.
 *
 * @param time The time to be formatted.
 * @param style The pattern for the string to be formatted in. [FormatStyle.SHORT] by default.
 * @param multiLine If the user uses the AM/PM system, whether AM/PM should be displayed on a new line or not.
 */
fun formatTime(
    time: LocalTime,
    style: FormatStyle = FormatStyle.SHORT,
    multiLine: Boolean = false,
): String {
    val formattedTime = time.toJavaLocalTime().format(DateTimeFormatter.ofLocalizedTime(style))
    if (!multiLine) return formattedTime

    val strings = formattedTime.split(" ")
    return if (strings.count() == 1) {
        strings[0]
    } else {
        "${strings[0]}\n${strings[1]}"
    }
}

/**
 * Formats a [LocalDate] to a human-readable string.
 * Defaults to [FormatStyle.LONG] if no pattern or style is given.
 *
 * @param date The date to be formatted.
 * @param pattern The pattern for the string to be formatted in.
 * @param style The pattern for the string to be formatted in. [FormatStyle.LONG] by default.
 */
fun formatDate(
    date: LocalDate,
    pattern: String = "",
    style: FormatStyle = FormatStyle.LONG,
): String {
    return if (pattern.isBlank()) {
        DateTimeFormatter.ofLocalizedDate(style).format(date.toJavaLocalDate())
    } else {
        DateTimeFormatter.ofPattern(pattern).format(date.toJavaLocalDate())
    }
}

/**
 * Formats a [LocalDate] to a human-readable string.
 * Defaults to [FormatStyle.LONG] if no pattern or style is given.
 *
 * @param date The date to be formatted.
 * @param pattern The pattern for the string to be formatted in.
 * @param style The pattern for the string to be formatted in. [FormatStyle.LONG] by default.
 */
fun formatDate(
    date: Instant,
    pattern: String = "",
    style: FormatStyle = FormatStyle.LONG,
): String = formatDate(date.toLocalDateTime(TimeZone.UTC).date, pattern, style)

/**
 * Creates a Kotlinx [LocalTime] object with the system's current timestamp.
 *
 * @return the current timestamp.
 */
fun LocalTime.Companion.now() = java.time.LocalTime.now().toKotlinLocalTime()

/**
 * Creates a Kotlinx [LocalDate] object with the system's current date.
 *
 * @return The systems current date with offset [TimeZone.UTC]
 */
fun LocalDate.Companion.now() = Clock.System.now().toLocalDateTime(TimeZone.UTC).date

/**
 * Converts a timestamp that represents the selected date start of the day in UTC milliseconds from the epoch to a [LocalDate] object.
 *
 * @return the date based off the timestamp.
 */
fun Long.toLocalDate() = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date

/**
 * Converts a Kotlinx [LocalDate] object to the number of milliseconds in UTC from the epoch.
 */
fun LocalDate.toEpochMilli() =
    this.toJavaLocalDate()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant().toEpochMilli()
