package com.nurhidayaatt.googlemapscompose.presentation.content

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nurhidayaatt.googlemapscompose.presentation.MapsViewModel

@Composable
fun MapErrorMessageBar(viewModel: MapsViewModel) {
    val enterAnimation: EnterTransition = expandVertically(
        animationSpec = tween(durationMillis = 300),
        expandFrom = Alignment.Top
    )
    val exitAnimation: ExitTransition = shrinkVertically(
        animationSpec = tween(durationMillis = 300),
        shrinkTowards = Alignment.Top
    )

    AnimatedVisibility(
        visible = viewModel.state.googleMapsApiNotSupported,
        enter = enterAnimation,
        exit = exitAnimation
    ) {
        ErrorMessageBar(errorMessage = "You can't make map requests")
    }
}

@Composable
fun ErrorMessageBar(errorMessage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.error)
            .padding(vertical = 12.dp)
            .padding(horizontal = 12.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(4f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Message Bar Icon",
                tint = MaterialTheme.colors.onError
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.onError,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@Preview
internal fun ErrorMessageBarPreview() {
    ErrorMessageBar(
        errorMessage = "You can't make map requests"
    )
}