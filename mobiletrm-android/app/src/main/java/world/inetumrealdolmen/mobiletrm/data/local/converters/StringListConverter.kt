package world.inetumrealdolmen.mobiletrm.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import world.inetumrealdolmen.mobiletrm.data.model.Task

/**
 * Converter class to convert to and from a list for use with RoomDB.
 */
class StringListConverter {
    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)

    @TypeConverter
    fun fromA(value: List<Task>) = Json.encodeToString(value)

    @TypeConverter
    fun toA(value: String) = Json.decodeFromString<List<Task>>(value)
}
