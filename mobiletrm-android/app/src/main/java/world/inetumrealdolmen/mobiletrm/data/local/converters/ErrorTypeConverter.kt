package world.inetumrealdolmen.mobiletrm.data.local.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType

/**
 * Converter class to convert to and from [ErrorType]s
 */
class ErrorTypeConverter {
    @TypeConverter
    fun fromError(value: ErrorType) = Json.encodeToString(value)

    @TypeConverter
    fun toError(value: String) = Json.decodeFromString<ErrorType>(value)
}
