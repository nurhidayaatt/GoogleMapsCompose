package com.nurhidayaatt.googlemapscompose.presentation.util

import com.google.maps.android.compose.MapProperties
import com.nurhidayaatt.googlemapscompose.presentation.util.PermissionStatus.GRANTED

data class MapState(
    val googleMapsApiNotSupported: Boolean = false,
    val needShowMyLocation: Boolean = true,
    val mapPermissionsStatus: PermissionStatus = GRANTED,
    val gpsEnabled: Boolean = false,
    val showAlertDialog: Boolean = true,
    val showMapStyle: Boolean = false,
    val properties: MapProperties = MapProperties()
)