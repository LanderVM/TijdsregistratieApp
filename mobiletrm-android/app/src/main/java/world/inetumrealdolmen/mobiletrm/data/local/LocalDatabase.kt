package world.inetumrealdolmen.mobiletrm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import world.inetumrealdolmen.mobiletrm.data.local.converters.DateConverters
import world.inetumrealdolmen.mobiletrm.data.local.converters.ErrorTypeConverter
import world.inetumrealdolmen.mobiletrm.data.local.converters.StringListConverter
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbProject
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbProjectDetails
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimeRegistration
import world.inetumrealdolmen.mobiletrm.data.local.entities.DbTimerEntry
import world.inetumrealdolmen.mobiletrm.data.local.entities.FilterPreferences
import world.inetumrealdolmen.mobiletrm.data.local.entities.SnackbarPreferences

/**
 * SQLite database, abstracted using Android RoomDB.
 *
 * @property dao The DAO to use to query in this database.
 */
@Database(
    entities = [
        DbTimeRegistration::class,
        DbTimerEntry::class,
        DbProject::class,
        DbProjectDetails::class,
        FilterPreferences::class,
        SnackbarPreferences::class,
    ],
    version = 5,
)
@TypeConverters(
    DateConverters::class,
    StringListConverter::class,
    ErrorTypeConverter::class,
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun dao(): MobileTRMDao

    abstract fun preferencesDao(): PreferencesDao
}
