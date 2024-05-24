package world.inetumrealdolmen.mobiletrm.ui.screen.geofence

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.altbeacon.bluetooth.BluetoothMedic
import world.inetumrealdolmen.mobiletrm.beacons.BeaconRangingSmoother
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.data.repository.impl.QuarkusRepository
import world.inetumrealdolmen.mobiletrm.ui.util.parseException
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class GeofenceViewModel
    @Inject
    constructor(
        private val repository: QuarkusRepository,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        companion object {
            const val SCAN_DURATION = 1000L
        }

        var apiState: ApiState by mutableStateOf(ApiState.Loading)

        private val beacons = MutableStateFlow<List<Region>>(emptyList())
        private val beaconsMap = ConcurrentHashMap<Region, Collection<Beacon>>()

        data class RSSIStats(
            val currentRSSI: Int,
            val minRSSI: Int,
            val maxRSSI: Int,
        )

        private val _beaconRSSIFlow = MutableStateFlow<List<Pair<Region, RSSIStats>>>(emptyList())
        val beaconRSSIFlow = _beaconRSSIFlow.asStateFlow()

        private val beaconRangingSmoother = BeaconRangingSmoother(SCAN_DURATION, 10)
        private val lastProcessedTimestamps = ConcurrentHashMap<Identifier, Long>()
        private val accumulatedRSSI = ConcurrentHashMap<Region, RSSIStats>()

        private val _isScanning = MutableStateFlow(false)
        val isScanning: StateFlow<Boolean> = _isScanning

        private lateinit var beaconManager: BeaconManager

        init {
            fetchBeacons()
        }

        private fun fetchBeacons() {
            apiState = ApiState.Loading
            viewModelScope.launch {
                try {
                    beacons.value =
                        listOf(
                            Region("BluePulse1", Identifier.parse("20FEF172-070C-CA90-E145-CEC0F88F71AC"), null, null),
                            Region("BluePulse2", Identifier.parse("4771157D-9EC9-C2B1-4245-2B0B1E17A35F"), null, null),
                            Region("BluePulse3", Identifier.parse("327A00B9-8D7D-9B9C-8B4E-C8A07EEF85E4"), null, null),
                        )
                    apiState = ApiState.Success
                } catch (e: Exception) {
                    apiState = parseException(e, "fetchProject")
                }
            }
        }

        fun toggleScanning() {
            if (_isScanning.value) {
                stopRecordingBeacons()
            } else {
                startRecordingBeacons()
            }
        }

        private fun startRecordingBeacons() {
            beaconManager = BeaconManager.getInstanceForApplication(context)

            val parser = BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
            parser.setHardwareAssistManufacturerCodes(arrayOf(0x004c).toIntArray())
            beaconManager.beaconParsers.add(parser)

            beaconManager.foregroundScanPeriod = SCAN_DURATION
            beaconManager.foregroundBetweenScanPeriod = 0L

            BluetoothMedic.getInstance().enablePeriodicTests(context, BluetoothMedic.SCAN_TEST + BluetoothMedic.TRANSMIT_TEST)

            for (beaconRegion in beacons.value) {
                setupBeaconScanning(beaconManager, beaconRegion)
            }
            _isScanning.value = true
        }

        private fun stopRecordingBeacons() {
            _isScanning.value = false
        }

        private fun setupBeaconScanning(
            beaconManager: BeaconManager,
            region: Region,
        ) {
            beaconManager.startMonitoring(region)
            beaconManager.startRangingBeacons(region)

            val regionViewModel = beaconManager.getRegionViewModel(region)
            regionViewModel.rangedBeacons.observeForever { beacons ->
                viewModelScope.launch {
                    beaconRangingSmoother.add(beacons)
                    beaconsMap[region] = beaconRangingSmoother.visibleBeacons
                    aggregateBeacons()
                }
            }
        }

        private fun aggregateBeacons() {
            val allBeacons = beaconsMap.values.flatten()
            val currentTime = System.currentTimeMillis()

            if (_isScanning.value) {
                allBeacons.forEach { beacon ->
                    val lastProcessedTime = lastProcessedTimestamps[beacon.id1] ?: 0
                    if (currentTime - lastProcessedTime >= SCAN_DURATION) {
                        val region = beacons.value.find { it.id1 == beacon.id1 }
                        if (region != null) {
                            val rssi = beacon.rssi
                            val stats = accumulatedRSSI.getOrPut(region) { RSSIStats(rssi, rssi, rssi) }

                            val newStats =
                                RSSIStats(
                                    currentRSSI = rssi,
                                    minRSSI = minOf(rssi, stats.minRSSI),
                                    maxRSSI = maxOf(rssi, stats.maxRSSI),
                                )

                            accumulatedRSSI[region] = newStats
                            lastProcessedTimestamps[beacon.id1] = currentTime
                            Log.i("GeofenceViewModel", "Beacon ${beacon.id1} with RSSI $rssi is within geofence ${region.uniqueId}")
                        }
                    }
                }
                _beaconRSSIFlow.value = accumulatedRSSI.map { it.toPair() }
            }
        }

        fun createGeofence() {
            Log.i("GeofenceViewModel", "Create geofence button clicked ${_beaconRSSIFlow.value}")
        }
    }
