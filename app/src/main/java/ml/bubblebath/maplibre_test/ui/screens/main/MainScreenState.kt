package ml.bubblebath.maplibre_test.ui.screens.main

data class MainScreenState(
    val isCompassVisible: Boolean = true,
    val isScaleBarVisible: Boolean = true,
    val layersList: List<String> = emptyList(),
    val isLayersDialogVisible: Boolean = false,
    val latitude: String = "-",
    val longitude: String = "-",
    val bearing: String = "-",
    val zoom: String = "-",
    val distanceBetweenTwoLast: String = "-",
    val totalDistance: String = "-",
    val layerOpacity: Float = 1f,
    val lineColor: Float = 0f
)
