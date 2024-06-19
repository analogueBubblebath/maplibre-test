package ml.bubblebath.maplibre_test.ui.screens.main

import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ml.bubblebath.maplibre_test.composable.MbTileSourceItem
import ml.bubblebath.maplibre_test.ui.composables.MapLibreView
import ml.bubblebath.maplibre_test.ui.composables.OnMapButton
import ml.bubblebath.maplibre_test.ui.composables.VerticalSlider
import ml.bubblebath.maplibre_test.ui.theme.MapButtonColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<MainScreenViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                context.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            }
        } else {
            // todo "VERSION.SDK_INT < R"
        }
    }


    Box(modifier = modifier) {
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            onMapReady = { mapLibreMap, scaleBarPlugin ->
                viewModel.handleIntent(
                    MainScreenIntent.MapReady(
                        mapLibreMap,
                        scaleBarPlugin
                    )
                )
            }
        )

        if (uiState.isLayersDialogVisible) {
            Dialog(onDismissRequest = { viewModel.handleIntent(MainScreenIntent.HideLayersDialog) }) {
                Surface(
                    modifier = Modifier
                        .width(500.dp)
                        .height(600.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(16.dp)) {
                        items(uiState.layersList) {
                            MbTileSourceItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                path = it,
                                onClick = { path ->
                                    viewModel.handleIntent(
                                        MainScreenIntent.AddLayer(
                                            path
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnMapButton(
                onClick = { viewModel.handleIntent(MainScreenIntent.ShowLayersDialog) }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
            }

            OnMapButton(onClick = { viewModel.handleIntent(MainScreenIntent.SavePath) }) {
                Icon(imageVector = Icons.Filled.Create, contentDescription = null)
            }

            OnMapButton(onClick = { viewModel.handleIntent(MainScreenIntent.LoadPath) }) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null)
            }

            VerticalSlider(modifier = Modifier.width(200.dp), value = uiState.lineColor, onValueChange ={
                viewModel.handleIntent(MainScreenIntent.ChangeLineColor(newValue = it))
            })
        }

        Column(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 152.dp)
                .align(Alignment.BottomStart)
                .background(MapButtonColor),
        ) {
            Text(text = "Lat: ${uiState.latitude}")
            Text(text = "Lon: ${uiState.longitude}")
            Text(text = "Zoom: ${uiState.zoom}")
        }
        Column(
            modifier = Modifier
                .padding(top = 100.dp)
                .align(Alignment.TopStart)
                .background(MapButtonColor)
        ) {
            Text(text = "Distance (km)")
            Text(text = "Two last: ${uiState.distanceBetweenTwoLast}")
            Text(text = "Total: ${uiState.totalDistance}")
        }
        Column(
            modifier = Modifier
                .padding(top = 100.dp)
                .align(Alignment.TopEnd)
                .background(MapButtonColor),
        ) {
            Text(modifier = Modifier.padding(8.dp), text = "Bearing: ${uiState.bearing}")
        }
        Icon(
            modifier = Modifier.align(Alignment.Center),
            imageVector = Icons.Outlined.Add,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, bottom = 52.dp)
                .align(Alignment.BottomStart),
        ) {
            OnMapButton(onClick = { viewModel.handleIntent(MainScreenIntent.ClearPoints) }) {
                Text(text = "Clear distance points")
            }
            OnMapButton(onClick = { viewModel.handleIntent(MainScreenIntent.ResetCamera) }) {
                Text(text = "Reset camera")
            }
        }

        VerticalSlider(
            modifier = Modifier
                .width(350.dp)
                .align(Alignment.CenterStart),
            value = uiState.layerOpacity,
            onValueChange = { viewModel.handleIntent(MainScreenIntent.ChangeLayerOpacity(newOpacity = it)) })

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(150.dp)
                .padding(bottom = 52.dp), horizontalAlignment = Alignment.End
        ) {
            OnMapButton(
                onClick = { viewModel.handleIntent(MainScreenIntent.CompassVisibility) }) {
                if (uiState.isCompassVisible) {
                    Text(text = "Hide compass")
                } else {
                    Text(text = "Show compass")
                }
            }
            OnMapButton(
                onClick = { viewModel.handleIntent(MainScreenIntent.ScaleBarVisibility) }) {
                if (uiState.isScaleBarVisible) {
                    Text(text = "Hide scalebar")
                } else {
                    Text(text = "Show scalebar")
                }
            }
        }
    }
}
