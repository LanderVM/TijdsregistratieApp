package world.inetumrealdolmen.mobiletrm.ui.screen.geofence

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import world.inetumrealdolmen.mobiletrm.data.model.ApiState
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.Indicator
import world.inetumrealdolmen.mobiletrm.ui.common.indicator.IndicatorType
import world.inetumrealdolmen.mobiletrm.ui.common.navigation.NavScaffold

@Composable
fun GeofenceScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: GeofenceViewModel = hiltViewModel(),
) {
    val beaconRSSIList by viewModel.beaconRSSIFlow.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    NavScaffold(navController, modifier = modifier) { padding ->
        when (val state = viewModel.apiState) {
            is ApiState.Error -> {
                Indicator(
                    type = IndicatorType.ERROR,
                    error = state.details,
                )
            }
            ApiState.Loading -> {
                Indicator(IndicatorType.LOADING)
            }
            is ApiState.Success -> {
                Box(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(onClick = { viewModel.toggleScanning() }) {
                            Text(text = if (isScanning) "Stop Scanning" else "Start Scanning")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        if (isScanning) {
                            if (beaconRSSIList.isNullOrEmpty()) {
                                Text(text = "No Beacons found")
                            } else {
                                Text(text = "Beacons and their RSSI values:")
                                LazyColumn {
                                    items(beaconRSSIList) { (region, rssi) ->
                                        Text(
                                            text =
                                                "${region.uniqueId}: RSSI = ${rssi.currentRSSI} " +
                                                    "min = ${rssi.minRSSI} max = ${rssi.maxRSSI}",
                                        )
                                    }
                                }
                            }
                        } else {
                            if (beaconRSSIList.isNotEmpty()) {
                                Text(text = "Beacons and their RSSI values:")
                                LazyColumn {
                                    items(beaconRSSIList) { (region, rssi) ->
                                        Text(
                                            text =
                                                "${region.uniqueId}: RSSI = ${rssi.currentRSSI} " +
                                                    "min = ${rssi.minRSSI} max = ${rssi.maxRSSI}",
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.createGeofence() }) {
                                    Text(text = "Create geofence")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
