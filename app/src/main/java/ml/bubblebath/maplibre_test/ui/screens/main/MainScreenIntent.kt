package ml.bubblebath.maplibre_test.ui.screens.main

import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.plugins.scalebar.ScaleBarPlugin

sealed interface MainScreenIntent {
    data object ShowLayersDialog : MainScreenIntent
    data object HideLayersDialog : MainScreenIntent
    data class AddLayer(val sourcePath: String) : MainScreenIntent
    data class ChangeLayerOpacity(val newOpacity: Float) : MainScreenIntent
    data object ClearPoints : MainScreenIntent
    data object ResetCamera : MainScreenIntent
    data object CompassVisibility : MainScreenIntent
    data object ScaleBarVisibility : MainScreenIntent
    data class MapReady(val mapLibreMap: MapLibreMap, val scaleBarPlugin: ScaleBarPlugin) :
        MainScreenIntent
}
