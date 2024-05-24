package world.inetumrealdolmen.mobiletrm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import world.inetumrealdolmen.mobiletrm.ui.screen.timeregistrationsoverview.FilterType
import world.inetumrealdolmen.mobiletrm.ui.util.now

/**
 * The user's last filter selection in the time registrations overview page.
 */
@Entity(tableName = "filter_preferences")
data class FilterPreferences(
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    val localId: Long = 0,
    @ColumnInfo(name = "reference_date")
    val referenceDate: LocalDate = LocalDate.now(),
    @ColumnInfo(name = "selected_filter")
    val selectedFilter: FilterType = FilterType.Week,
    @ColumnInfo(name = "offset")
    val offset: DatePeriod = DatePeriod(),
)
