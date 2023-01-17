package com.nurhidayaatt.googlemapscompose.presentation.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.nurhidayaatt.googlemapscompose.presentation.MapsViewModel
import com.nurhidayaatt.googlemapscompose.presentation.util.MapEvent
import com.nurhidayaatt.googlemapscompose.presentation.util.PermissionStatus.PERMANENT_DENIED
import com.nurhidayaatt.googlemapscompose.presentation.util.PermissionStatus.SHOULD_SHOW_RATIONAL


@ExperimentalPermissionsApi
@Composable
fun MapAlertDialog(
    context: Context,
    mapPermissionState: MultiplePermissionsState,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    viewModel: MapsViewModel
) {
    if (viewModel.state.mapPermissionsStatus == SHOULD_SHOW_RATIONAL && viewModel.state.showAlertDialog) {
        Log.d("OpenDialog", "${viewModel.state.mapPermissionsStatus}")
        ShowDialog(
            title = "Permission Request",
            text = "This permission is important for this app. Please grant the permission.",
            confirmButtonText = "Confirm",
            confirmButton = {
                mapPermissionState.launchMultiplePermissionRequest()
            },
            viewModel = viewModel
        )
    }
    if (viewModel.state.mapPermissionsStatus == PERMANENT_DENIED && viewModel.state.showAlertDialog) {
        Log.d("OpenDialog", "${viewModel.state.mapPermissionsStatus}")
        ShowDialog(
            title = "Permission is permanently denied",
            text = "",
            confirmButtonText = "Confirm",
            confirmButton = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            },
            viewModel = viewModel
        )
    }
    if (!viewModel.state.gpsEnabled && viewModel.state.showAlertDialog) {
        Log.d("OpenDialog", "${viewModel.state.gpsEnabled}")
        ShowDialog(
            title = "GPS Off",
            text = "",
            confirmButtonText = "Confirm",
            confirmButton = {
                viewModel.onEvent(MapEvent.ShowAlertDialog(false))
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                launcher.launch(intent)
            },
            viewModel = viewModel
        )
    }
}

@Composable
inline fun ShowDialog(
    title: String,
    text: String,
    confirmButtonText: String? = null,
    crossinline confirmButton: () -> Unit? = {},
    viewModel: MapsViewModel,
) {
    AlertDialog(
        onDismissRequest = {
            viewModel.onEvent(MapEvent.ShowAlertDialog(false))
        },
        confirmButton = {
            confirmButtonText?.let { text ->
                TextButton(
                    onClick = {
                        confirmButton()
                    }
                ) {
                    Text(text)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.onEvent(MapEvent.ShowAlertDialog(false)) }
            ) {
                Text("Close")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun OpenDialogPreview() {
    ShowDialog(
        title = "Permission is permanently denied",
        text = "This permission is important for this app. Please grant the permission.",
        viewModel = viewModel()
    )
}