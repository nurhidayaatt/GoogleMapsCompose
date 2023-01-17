package com.nurhidayaatt.googlemapscompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nurhidayaatt.googlemapscompose.presentation.util.MapEvent
import com.nurhidayaatt.googlemapscompose.presentation.util.MapState
import com.nurhidayaatt.googlemapscompose.presentation.util.PermissionStatus.GRANTED

class MapsViewModel : ViewModel() {

    var state by mutableStateOf(MapState())

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.GoogleMapsApiNotSupported -> {
                state = state.copy(
                    googleMapsApiNotSupported = true,
                    showMapStyle = false
                )
            }
            is MapEvent.ShowCurrentLocation -> {
                state = state.copy(
                    needShowMyLocation = event.showCurrentLocation,
                    showAlertDialog = true
                )
            }
            is MapEvent.MapPermissionsStatus -> {
                state = state.copy(
                    properties = state.properties.copy(
                        isMyLocationEnabled = state.mapPermissionsStatus == GRANTED
                    ),
                    mapPermissionsStatus = event.status
                )
            }
            is MapEvent.GpsOn -> {
                state = state.copy(
                    gpsEnabled = event.gpsOn
                )
            }
            is MapEvent.ShowAlertDialog -> {
                state = state.copy(
                    showAlertDialog = event.showAlertDialog
                )
            }
            is MapEvent.ShowMapType -> {
                state = state.copy(showMapStyle = event.showMapType)
            }
            is MapEvent.MapStyle -> {
                state = state.copy(
                    properties = state.properties.copy(
                        mapType = event.type
                    )
                )
            }
        }
    }
}