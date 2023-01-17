package com.nurhidayaatt.googlemapscompose.presentation.content

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.MapType
import com.nurhidayaatt.googlemapscompose.R
import com.nurhidayaatt.googlemapscompose.presentation.MapsViewModel
import com.nurhidayaatt.googlemapscompose.presentation.util.MapEvent

@Composable
fun MapStyleContent(viewModel: MapsViewModel) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        FloatingActionButton(
            onClick = {
                viewModel.onEvent(MapEvent.ShowMapType(true))
            },
            modifier = Modifier
                .size(40.dp)
                .alpha(if (viewModel.state.googleMapsApiNotSupported) 0f else 1f)
                .align(Alignment.TopEnd)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_layers),
                contentDescription = "Map Style Button"
            )
        }

        val enterAnimation: EnterTransition = expandIn(
            animationSpec = tween(300),
            expandFrom = Alignment.TopEnd
        )
        val exitAnimation: ExitTransition = shrinkOut(
            animationSpec = tween(300),
            shrinkTowards = Alignment.TopEnd
        )

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopEnd),
            visible = viewModel.state.showMapStyle,
            enter = enterAnimation,
            exit = exitAnimation
        ) {
            MapStyleCard(viewModel = viewModel)
        }
    }
}

@Composable
fun MapStyleCard(viewModel: MapsViewModel) {
    Card(
        modifier = Modifier.width(250.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Map Type",
                fontSize = MaterialTheme.typography.subtitle1.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                color = if (viewModel.state.properties.mapType == MapType.NORMAL) Color.Blue else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.type_default),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(onClick = {
                                    viewModel.onEvent(MapEvent.MapStyle(MapType.NORMAL))
                                })
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Default", fontSize = 12.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                color = if (viewModel.state.properties.mapType == MapType.TERRAIN) Color.Blue else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.type_terrain),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(onClick = {
                                    viewModel.onEvent(MapEvent.MapStyle(MapType.TERRAIN))
                                })
                                .align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Terrain", fontSize = 12.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                color = if (viewModel.state.properties.mapType == MapType.HYBRID) Color.Blue else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.type_satellite),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clickable(onClick = {
                                    viewModel.onEvent(MapEvent.MapStyle(MapType.HYBRID))
                                }).align(Alignment.Center)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Satellite", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview
@Composable
fun MapStyleCardPreview() {
    MapStyleCard(viewModel = viewModel())
}