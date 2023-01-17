package com.nurhidayaatt.googlemapscompose.presentation.util

import com.google.maps.android.compose.MapType

sealed interface MapEvent {
    object GoogleMapsApiNotSupported: MapEvent
    data class ShowCurrentLocation(val showCurrentLocation: Boolean): MapEvent
    data class MapPermissionsStatus(val status: PermissionStatus): MapEvent
    data class ShowAlertDialog(val showAlertDialog: Boolean): MapEvent
    data class GpsOn(val gpsOn: Boolean): MapEvent
    data class ShowMapType(val showMapType: Boolean): MapEvent
    data class MapStyle(val type: MapType): MapEvent
}