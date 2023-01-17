package com.nurhidayaatt.googlemapscompose.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nurhidayaatt.googlemapscompose.R
import com.nurhidayaatt.googlemapscompose.presentation.content.MapAlertDialog
import com.nurhidayaatt.googlemapscompose.presentation.content.MapErrorMessageBar
import com.nurhidayaatt.googlemapscompose.presentation.content.MapStyleContent
import com.nurhidayaatt.googlemapscompose.presentation.util.MapEvent
import com.nurhidayaatt.googlemapscompose.presentation.util.PermissionStatus.*
import com.nurhidayaatt.googlemapscompose.presentation.util.isPermanentlyDenied
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@Composable
fun MapScreen(viewModel: MapsViewModel = viewModel()) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            checkGPS(context, viewModel)
        }

    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                mapToolbarEnabled = false
            )
        )
    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState()

    checkGPS(context = context, viewModel = viewModel)

    CheckServicesAndPermissions(
        context = context,
        lifecycleOwner = lifecycleOwner,
        mapPermissionState = mapPermissionState,
        cameraPositionState = cameraPositionState,
        viewModel = viewModel
    )

    MapContent(
        scaffoldState = scaffoldState,
        mapUiSettings = mapUiSettings,
        cameraPositionState = cameraPositionState,
        viewModel = viewModel
    )

    MapAlertDialog(
        context = context,
        mapPermissionState = mapPermissionState,
        launcher = launcher,
        viewModel = viewModel
    )

    showCurrentLocation(
        context = context,
        coroutineScope = coroutineScope,
        cameraPositionState = cameraPositionState,
        viewModel = viewModel
    )
}

@ExperimentalPermissionsApi
@Composable
fun CheckServicesAndPermissions(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    mapPermissionState: MultiplePermissionsState,
    cameraPositionState: CameraPositionState,
    viewModel: MapsViewModel,
) {
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    val available =
                        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
                    if (available == ConnectionResult.SUCCESS) {
                        Log.d("CheckServices", "Google Play Services Available")
                        mapPermissionState.launchMultiplePermissionRequest()
                        if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
                            Log.e("CheckServices", "An error occurred")
                        }
                    } else {
                        Log.e("CheckServices", "Google Play Services Unavailable")
                        viewModel.onEvent(MapEvent.GoogleMapsApiNotSupported)
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    when {
        mapPermissionState.allPermissionsGranted -> {
            Log.d("CheckPermissions", "Permissions Granted")
            viewModel.onEvent(MapEvent.MapPermissionsStatus(GRANTED))
        }
        mapPermissionState.shouldShowRationale -> {
            Log.d("CheckPermissions",
                "This permission is important for this app. Please grant the permission.")
            viewModel.onEvent(MapEvent.MapPermissionsStatus(SHOULD_SHOW_RATIONAL))
        }
        mapPermissionState.isPermanentlyDenied() -> {
            // TODO: don't change permission status If it's the first time the user lands on this feature
            Log.d("CheckPermissions", "This permission is permanently denied")
            viewModel.onEvent(MapEvent.MapPermissionsStatus(PERMANENT_DENIED))
        }
    }

    if (cameraPositionState.isMoving) {
        viewModel.onEvent(MapEvent.ShowMapType(showMapType = false))
    }
}

fun checkGPS(
    context: Context,
    viewModel: MapsViewModel
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        Log.i("checkMap", "GPS On")
        viewModel.onEvent(MapEvent.GpsOn(gpsOn = true))
    } else {
        Log.i("checkMap", "GPS Off")
        viewModel.onEvent(MapEvent.GpsOn(gpsOn = false))
    }
}

@Composable
fun MapContent(
    scaffoldState: ScaffoldState,
    mapUiSettings: MapUiSettings,
    cameraPositionState: CameraPositionState,
    viewModel: MapsViewModel,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(MapEvent.ShowCurrentLocation(showCurrentLocation = true))
                },
                modifier = Modifier.alpha(if (viewModel.state.googleMapsApiNotSupported) 0f else 1f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_current_location),
                    contentDescription = "Current Location Button"
                )
            }
        }
    ) {
        GoogleMap(
            modifier = Modifier
                .alpha(if (viewModel.state.googleMapsApiNotSupported) 0f else 1f)
                .fillMaxSize(),
            properties = viewModel.state.properties,
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState,
            contentPadding = it,
            onMapClick = { viewModel.onEvent(MapEvent.ShowMapType(showMapType = false)) },
        ) {

            MarkerInfoWindow(
                state = MarkerState(
                    LatLng(1.35, 103.87)
                ),
                title = "Marker 1",
                snippet = "don't click this",
                onInfoWindowClick = {
                    viewModel.onEvent(MapEvent.GoogleMapsApiNotSupported)
                }
            ) {
                // TODO: custom info window for marker
            }
        }
        MapStyleContent(viewModel = viewModel)
        MapErrorMessageBar(viewModel = viewModel)
    }
}

@ExperimentalPermissionsApi
@SuppressLint("MissingPermission")
fun showCurrentLocation(
    context: Context,
    coroutineScope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    viewModel: MapsViewModel,
) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    if (
        viewModel.state.gpsEnabled &&
        viewModel.state.mapPermissionsStatus == GRANTED &&
        viewModel.state.needShowMyLocation
    ) {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let {
                    Log.d("currentLocation", "Lat: ${it.latitude}, Lgn: ${it.longitude}")
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(task.result.latitude, task.result.longitude),
                                18f
                            )
                        )
                    }
                } ?: run {
                    // TODO: handle when last location return null
                    Log.i("currentLocation", "null")
                    Toast.makeText(context, "current location null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("currentLocation", task.exception.toString())
            }
        }
        viewModel.onEvent(MapEvent.ShowCurrentLocation(showCurrentLocation = false))
    }
}

