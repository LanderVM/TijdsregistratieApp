package world.inetumrealdolmen.mobiletrm.data.local.converters

import androidx.room.TypeConverter
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Converter class to convert to and from a Kotlinx date/time objects for use with RoomDB.
 */
class DateConverters {
    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime) = Json.encodeToString(value)

    @TypeConverter
    fun toLocalDateTime(value: String) = Json.decodeFromString<LocalDateTime>(value)

    @TypeConverter
    fun fromLocalDate(value: LocalDate?) = Json.encodeToString(value)

    @TypeConverter
    fun toLocalDate(value: String) = Json.decodeFromString<LocalDate?>(value)

    @TypeConverter
    fun fromDatePeriod(value: DatePeriod) = Json.encodeToString(value)

    @TypeConverter
    fun toDatePeriod(value: String) = Json.decodeFromString<DatePeriod>(value)
}
