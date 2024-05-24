package world.inetumrealdolmen.mobiletrm.beacons

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.altbeacon.bluetooth.BluetoothMedic
import world.inetumrealdolmen.mobiletrm.MainActivity
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.local.workers.BluetoothBeaconScanningWorker
import world.inetumrealdolmen.mobiletrm.ui.util.NotificationType
import world.inetumrealdolmen.mobiletrm.ui.util.displayNotification
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class BeaconManagerSetup
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            const val SCAN_DURATION = 3000L
            const val TAG = "BeaconReference"
            var isScanning = false
            const val GEOFENCE_MIN_DURATION = 30000L
        }

        private val regionBeacon1 = Region("BluePulse1", Identifier.parse("20FEF172-070C-CA90-E145-CEC0F88F71AC"), null, null)
        private val regionBeacon2 = Region("BluePulse2", Identifier.parse("4771157D-9EC9-C2B1-4245-2B0B1E17A35F"), null, null)
        private val regionBeacon3 = Region("BluePulse3", Identifier.parse("327A00B9-8D7D-9B9C-8B4E-C8A07EEF85E4"), null, null)
        private val kamerGeofence = listOf(regionBeacon1, regionBeacon2, regionBeacon3)

        private val beaconRangingSmoother = BeaconRangingSmoother(SCAN_DURATION)
        private val beaconsMap = ConcurrentHashMap<Region, Collection<Beacon>>()
        private var geofenceEntryTimestamp: Long? = null
        private lateinit var beaconManager: BeaconManager

        fun initialize() {
            beaconManager = BeaconManager.getInstanceForApplication(context)
            if (!beaconManager.isAnyConsumerBound && !isScanning) {
                beaconManager.setEnableScheduledScanJobs(true)
                val parser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
                parser.setHardwareAssistManufacturerCodes(arrayOf(0x004c).toIntArray())
                beaconManager.beaconParsers.add(parser)

                beaconManager.foregroundScanPeriod = SCAN_DURATION
                beaconManager.foregroundBetweenScanPeriod = 10000L
                beaconManager.backgroundScanPeriod = SCAN_DURATION
                beaconManager.backgroundBetweenScanPeriod = 30000L

                BluetoothMedic.getInstance().enablePeriodicTests(context, BluetoothMedic.SCAN_TEST + BluetoothMedic.TRANSMIT_TEST)

                for (beaconRegion: Region in kamerGeofence) {
                    setupBeaconScanning(beaconManager, beaconRegion)
                }
                isScanning = true
            } else {
                Log.d(TAG, "BeaconManager is already configured and running.")
            }
        }

        fun stop() {
            beaconManager.stopMonitoring(regionBeacon1)
            beaconManager.stopMonitoring(regionBeacon2)
            beaconManager.stopMonitoring(regionBeacon3)
            beaconManager.stopRangingBeacons(regionBeacon1)
            beaconManager.stopRangingBeacons(regionBeacon2)
            beaconManager.stopRangingBeacons(regionBeacon3)
        }

        private fun setupBeaconScanning(
            beaconManager: BeaconManager,
            region: Region,
        ) {
            try {
                setupForegroundService(beaconManager)
            } catch (e: SecurityException) {
                Log.d(TAG, "Not setting up foreground service scanning until location permission granted by user")
                return
            }

            beaconManager.startMonitoring(region)
            beaconManager.startRangingBeacons(region)

            val regionViewModel = beaconManager.getRegionViewModel(region)
            regionViewModel.rangedBeacons.observeForever { beacons ->
                beaconsMap[region] = beacons
                aggregateBeacons()
            }
        }

        private fun setupForegroundService(beaconManager: BeaconManager) {
            val builder =
                Notification.Builder(context, NotificationType.BEACON.id.toString())
                    .setSmallIcon(NotificationType.BEACON.displayIcon)
                    .setContentTitle(context.getString(R.string.notifications_beacons_displayName))
                    .setContentText(context.getString(R.string.notifications_beacons_scanning))
                    .setOngoing(true)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
            builder.setContentIntent(pendingIntent)
            Log.d(TAG, "Calling enableForegroundServiceScanning")
            beaconManager.enableForegroundServiceScanning(builder.build(), NotificationType.BEACON.id)
            Log.d(TAG, "Back from enableForegroundServiceScanning")
        }

        private fun aggregateBeacons() {
            val allBeacons = beaconsMap.values.flatten()
            CoroutineScope(Dispatchers.Default).launch {
                beaconRangingSmoother.add(allBeacons)
                val visibleBeacons = beaconRangingSmoother.visibleBeacons
                val smoothedRSSI = visibleBeacons.associate { it.id1.toString() to it.rssi }
                var text = ""

                Log.i(TAG, "Aggregated beacons: $visibleBeacons")

                visibleBeacons.forEach { beacon ->
                    Log.i(TAG, "(RSSI: ${beacon.rssi}) Beacon ${beacon.id1}")
                    text += "${beacon.id1.toString().substring(0, 3)}: ${beacon.rssi} | "
                }

                val isInside = isUserWithinRegion(smoothedRSSI, beacon1, beacon2, beacon3)
                val isNearbyAllBeacons = visibleBeacons.size == 3

                if (isNearbyAllBeacons && !isInside) {
                    val currentTime = System.currentTimeMillis()
                    if (geofenceEntryTimestamp == null) {
                        geofenceEntryTimestamp = currentTime
                    } else if (currentTime - geofenceEntryTimestamp!! >= GEOFENCE_MIN_DURATION) {
                        startTimer()
                    }
                } else {
                    geofenceEntryTimestamp = null
                    stopTimer()
                }

                // Useful for debugging beacons
                context.displayNotification(
                    NotificationType.TIMER,
                    title = "Beacon",
                    ongoing = true,
                    text = "Inside: $isInside, Nearby all beacons: $isNearbyAllBeacons || $text",
                )
            }
        }

        private fun startTimer() {
            val request =
                OneTimeWorkRequestBuilder<BluetoothBeaconScanningWorker>()
                    .setConstraints(Constraints(requiresBatteryNotLow = false))
                    .setInputData(workDataOf("isOutside" to false, "action" to "start"))
                    .build()

            WorkManager.getInstance(context)
                .beginUniqueWork(
                    "bluetooth_beacon_scanning",
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
                .enqueue()
        }

        private fun stopTimer() {
            val request =
                OneTimeWorkRequestBuilder<BluetoothBeaconScanningWorker>()
                    .setConstraints(Constraints(requiresBatteryNotLow = false))
                    .setInputData(workDataOf("isOutside" to true, "action" to "stop"))
                    .build()

            WorkManager.getInstance(context)
                .beginUniqueWork(
                    "bluetooth_beacon_scanning",
                    ExistingWorkPolicy.REPLACE,
                    request,
                )
                .enqueue()
        }
    }

data class BeaconValues(val min: Int, val max: Int)

val beacon1 = BeaconValues(min = -67, max = -81)
val beacon2 = BeaconValues(min = -77, max = -87)
val beacon3 = BeaconValues(min = -54, max = -35)

fun isWithinRange(
    value: Int,
    min: Int,
    max: Int,
): Boolean {
    return value in min..max
}

fun isUserWithinRegion(
    smoothedRSSI: Map<String, Int>,
    point1: BeaconValues,
    point2: BeaconValues,
    point3: BeaconValues,
): Boolean {
    val beacon1RSSI = smoothedRSSI["327A00B9-8D7D-9B9C-8B4E-C8A07EEF85E4"]
    val beacon2RSSI = smoothedRSSI["20fef172-070c-ca90-e145-cec0f88f71ac"]
    val beacon3RSSI = smoothedRSSI["4771157d-9ec9-c2b1-4245-2b0b1e17a35f"]

    return if (beacon1RSSI != null && beacon2RSSI != null && beacon3RSSI != null) {
        val minBeacon1 = point1.min
        val maxBeacon1 = point1.max

        val minBeacon2 = point2.min
        val maxBeacon2 = point2.max

        val minBeacon3 = point3.min
        val maxBeacon3 = point3.max
        Log.i(
            "isWithinRange",
            "1: $beacon1RSSI ${isWithinRange(beacon1RSSI, minBeacon1, maxBeacon1)}" +
                ",2: $beacon2RSSI ${isWithinRange(beacon2RSSI, minBeacon2, maxBeacon2)}, " +
                "3: $beacon3RSSI ${isWithinRange(beacon3RSSI, minBeacon3, maxBeacon3)}",
        )

        isWithinRange(beacon1RSSI, minBeacon1, maxBeacon1) &&
            isWithinRange(beacon2RSSI, minBeacon2, maxBeacon2) &&
            isWithinRange(beacon3RSSI, minBeacon3, maxBeacon3)
    } else {
        false
    }
}
