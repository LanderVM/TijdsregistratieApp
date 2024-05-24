package world.inetumrealdolmen.mobiletrm.beacons

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.altbeacon.beacon.Beacon
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class BeaconRangingSmoother(
    private var smoothingWindowMillis: Long,
    private var windowSize: Int = 5,
) {
    private var beacons: MutableList<Beacon> = CopyOnWriteArrayList()
    private val rssiMap: ConcurrentHashMap<String, EnhancedKalmanFilter> = ConcurrentHashMap()

    val visibleBeacons: List<Beacon>
        get() {
            val currentTime = System.currentTimeMillis()
            return beacons.filter { currentTime - it.lastCycleDetectionTimestamp < smoothingWindowMillis }
        }

    suspend fun add(detectedBeacons: Collection<Beacon>): BeaconRangingSmoother {
        val currentTime = System.currentTimeMillis()
        val updatedBeacons = beacons.filter { currentTime - it.lastCycleDetectionTimestamp < smoothingWindowMillis }.toMutableList()

        detectedBeacons.forEach { beacon ->
            beacon.lastCycleDetectionTimestamp = currentTime
            val originalRssi = beacon.rssi
            val smoothedRssi = smoothRssi(beacon)
            beacon.rssi = smoothedRssi

            // Log the original and smoothed RSSI values
            Log.i("BeaconRangingSmoother", "Beacon: ${beacon.id1} Original RSSI: $originalRssi, Smoothed RSSI: $smoothedRssi")

            updatedBeacons.removeAll { it.id1 == beacon.id1 && it.id2 == beacon.id2 && it.id3 == beacon.id3 }
            updatedBeacons.add(beacon)
        }

        beacons = updatedBeacons
        return this
    }

    private suspend fun smoothRssi(beacon: Beacon): Int {
        return withContext(Dispatchers.Default) {
            val beaconKey = beacon.id1.toString()
            val kalmanFilter = rssiMap.getOrPut(beaconKey) { EnhancedKalmanFilter(beacon.rssi.toDouble(), windowSize = windowSize) }
            kalmanFilter.update(beacon.rssi)
        }
    }
}

class EnhancedKalmanFilter(
    initialEstimate: Double,
    private var processNoise: Double = 1.0,
    private var measurementNoise: Double = 1.0,
    private var processNoiseBase: Double = 1.0,
    private var measurementNoiseBase: Double = 1.0,
    private var adaptationFactor: Double = 0.01,
    private val windowSize: Int = 5,
) {
    private var estimate: Double = initialEstimate
    private var estimateError: Double = 1.0
    private val rssiHistory: MutableList<Int> = CopyOnWriteArrayList()

    fun update(measurement: Int): Int {
        Log.i("EnhancedKalmanFilter", "Measurement received: $measurement")
        // Update the RSSI history
        if (rssiHistory.size >= windowSize) {
            rssiHistory.removeAt(0)
        }
        rssiHistory.add(measurement)

        // Ensure that the list is not empty before sorting
        if (rssiHistory.isNotEmpty()) {
            // Calculate the median RSSI value
            val sortedRssiHistory = rssiHistory.sorted()
            val medianRssi = sortedRssiHistory[sortedRssiHistory.size / 2]

            // Kalman filter update
            val kalmanGain = estimateError / (estimateError + measurementNoise)
            estimate += kalmanGain * (medianRssi - estimate)
            estimateError = (1 - kalmanGain) * estimateError + processNoise

            // Adjust process noise and measurement noise based on the rate of change
            val changeRate = abs(medianRssi - estimate)
            processNoise = processNoiseBase + adaptationFactor * changeRate
            measurementNoise = measurementNoiseBase + adaptationFactor * changeRate

            Log.i("EnhancedKalmanFilter", "Updated estimate: $estimate")
            Log.i("EnhancedKalmanFilter", "RSSI history: $rssiHistory")
            Log.i("EnhancedKalmanFilter", "Process noise: $processNoise, Measurement noise: $measurementNoise")

            return estimate.toInt()
        } else {
            // If the list is empty, return the initial estimate as a fallback
            Log.i("EnhancedKalmanFilter", "Returning initial estimate as fallback: $estimate")
            return estimate.toInt()
        }
    }
}
