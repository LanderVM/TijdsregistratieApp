package world.inetumrealdolmen.mobiletrm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The app's snackbar state.
 */
@Entity(tableName = "snackbar_preferences")
data class SnackbarPreferences(
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    val localId: Long = 0,
    @ColumnInfo(name = "time_registration_created")
    val timeRegistrationCreated: Boolean = false,
    @ColumnInfo(name = "time_registration_updated")
    val timeRegistrationUpdated: Boolean = false,
    @ColumnInfo(name = "time_registration_deleted")
    val timeRegistrationDeleted: Boolean = false,
)
