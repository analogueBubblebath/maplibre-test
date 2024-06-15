package ml.bubblebath.maplibre_test.ui.composables

import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.plugins.scalebar.ScaleBarOptions
import org.maplibre.android.plugins.scalebar.ScaleBarPlugin
import java.io.File

@Composable
fun MapLibreView(
    modifier: Modifier = Modifier,
    onMapReady: (MapLibreMap, ScaleBarPlugin) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapLibre.getInstance(context)
            MapView(context).apply {
                this.getMapAsync { mapLibreMap ->
                    val scaleBarPlugin = ScaleBarPlugin(this, mapLibreMap)
                    val scaleBarOptions = ScaleBarOptions(context)
                    with(scaleBarOptions) {
                        setMetricUnit(true)
                        setMarginTop(150f)
                    }
                    scaleBarPlugin.create(scaleBarOptions)
                    onMapReady(mapLibreMap, scaleBarPlugin)
                }
            }
        })
}
