package com.nurhidayaatt.googlemapscompose.presentation.util

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@ExperimentalPermissionsApi
fun MultiplePermissionsState.isPermanentlyDenied(): Boolean {
    return !shouldShowRationale && !allPermissionsGranted
}