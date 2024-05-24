package world.inetumrealdolmen.mobiletrm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import world.inetumrealdolmen.mobiletrm.ui.util.now

/**
 * The user's time entries for the timer functionality.
 */
@Entity(tableName = "timer_entry")
data class DbTimerEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "start_time")
    val startTime: LocalDateTime,
    @ColumnInfo(name = "end_time")
    val endTime: LocalDateTime = LocalDateTime(LocalDate.now(), LocalTime(0, 0)),
)
