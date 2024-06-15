package ml.bubblebath.maplibre_test.ui.screens.main

import android.os.Environment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ml.bubblebath.maplibre_test.model.DistanceCalculator
import ml.bubblebath.maplibre_test.model.DistanceUnits
import ml.bubblebath.maplibre_test.model.TileSetScheme
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdate
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.plugins.scalebar.ScaleBarPlugin
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyValue
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import java.util.Locale

class MainScreenViewModel(private val distanceCalculator: DistanceCalculator) : ViewModel() {
    /*
    TODO
    1. Move replace magic strings with string constants
    2. Make UI for save/load GeoJSON
     */
    companion object {
        private const val OSM_TILES_URL = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
        private const val TILEJSON_VER = "2.0.0"
        private const val GEO_JSON_SOURCE_ID = "GEO_JSON_SOURCE_ID"
        private const val OSM_SOURCE_ID = "OSM_SOURCE_ID"
        private const val USER_SOURCE_ID = "USER_SOURCE_ID"
        private const val OSM_LAYER_ID = "OSM_LAYER_ID"
        private const val USER_LAYER_ID = "USER_LAYER_ID"
        private const val OSM_TILE_SIZE = 256
        private const val CIRCLE_LAYER_ID = "CIRCLE_LAYER_ID"
        private const val LINE_LAYER_ID = "LINE_LAYER_ID"
    }

    private lateinit var mapLibreMap: MapLibreMap
    private lateinit var scaleBarPlugin: ScaleBarPlugin

    private val _uiState = MutableStateFlow(MainScreenState())
    val uiState: StateFlow<MainScreenState> = _uiState

    private val tilesDir =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    //lateinit, потому что слои должны создаваться в UI потоке
    //у koin свои потоки для инициализации зависимостей
    //если создавать слои прям тут, то maplibre бросит исключение
    private lateinit var circleLayer: CircleLayer
    private lateinit var lineLayer: LineLayer
    private var userLayer: RasterLayer? = null
    private var userSource: RasterSource? = null

    /*
    Метод который вызывается один раз при создании View карты
    Тут происходит создание слоев (объекты слоев должны создаваться в UI потоке,
    иначе maplibre бросит исключение), сборка стиля карты и регистрация слушателей по событиям на карте
     */
    private fun onMapReady(mapLibreMap: MapLibreMap, scaleBarPlugin: ScaleBarPlugin) {
        this.mapLibreMap = mapLibreMap
        this.scaleBarPlugin = scaleBarPlugin
        this.circleLayer = CircleLayer(CIRCLE_LAYER_ID, GEO_JSON_SOURCE_ID)
        this.lineLayer = LineLayer(LINE_LAYER_ID, GEO_JSON_SOURCE_ID)

        mapLibreMap.uiSettings.setCompassFadeFacingNorth(false)
        mapLibreMap.uiSettings.setCompassMargins(0, 100, 16, 0)

        //если создавать RasterSource из URL, вместо TileSet, то невозможно будет задать схему (xyz/tms)
        val ts = TileSet(TILEJSON_VER, OSM_TILES_URL)
        ts.scheme = TileSetScheme.XYZ.toString()

        val styleBuilder = Style.Builder()
        styleBuilder.withSource(GeoJsonSource(GEO_JSON_SOURCE_ID))
        styleBuilder.withSource(RasterSource(OSM_SOURCE_ID, ts, OSM_TILE_SIZE))
        styleBuilder.withLayer(RasterLayer(OSM_LAYER_ID, OSM_SOURCE_ID))

        mapLibreMap.setStyle(styleBuilder) {
            mapLibreMap.addOnMapClickListener { onMapClick(it) }
            mapLibreMap.addOnCameraMoveListener { onCameraMove() }
        }
    }

    /*
    Метод обработчик событий, которые генерирует пользователь через взаимодействие с UI
     */
    fun handleIntent(intent: MainScreenIntent) {
        when (intent) {
            is MainScreenIntent.MapReady -> onMapReady(
                mapLibreMap = intent.mapLibreMap,
                scaleBarPlugin = intent.scaleBarPlugin
            )

            MainScreenIntent.CompassVisibility -> handleCompassVisibility()
            MainScreenIntent.ScaleBarVisibility -> handleScaleBarVisibility()
            MainScreenIntent.ResetCamera -> resetCamera()
            MainScreenIntent.ClearPoints -> clearPoints()
            MainScreenIntent.ShowLayersDialog -> showLayersDialog()
            MainScreenIntent.HideLayersDialog -> hideLayersDialog()
            is MainScreenIntent.AddLayer -> addLayer(intent.sourcePath)
            is MainScreenIntent.ChangeLayerOpacity -> changeLayerOpacity(intent.newOpacity)
        }
    }

    /*
    Метод который вызывается по нажатию на файл mbtiles из списка.
    Создаем источник из выбранного файла и стиль, далее добавляем их на карту
     */
    private fun addLayer(sourcePath: String) {
        val absolutePath = tilesDir.path + "/" + sourcePath
        userSource = RasterSource(USER_SOURCE_ID, "mbtiles://$absolutePath")
        userLayer = RasterLayer(USER_LAYER_ID, USER_SOURCE_ID)
        rebuildStyle()
    }

    /*
    Метод для пересборки стиля когда пользователь выбирает mbtiles из списка
     */
    private fun rebuildStyle() {
        mapLibreMap.getStyle {
            if (it.getSource(USER_SOURCE_ID) != null && it.getLayer(USER_LAYER_ID) != null) {
                it.removeLayer(USER_LAYER_ID)
                it.removeSource(USER_SOURCE_ID)
            } else {
                userSource?.let { source ->
                    it.addSource(source)
                    userLayer?.let { layer ->
                        it.addLayer(layer)
                    }
                }
            }
        }
    }

    /*
    Метод который вызывается при движении камеры.
    Обновляет координаты на плашке
     */
    private fun onCameraMove() {
        val latitude = mapLibreMap.cameraPosition.target?.latitude.toString()
        val longitude = mapLibreMap.cameraPosition.target?.longitude.toString()
        val bearing = mapLibreMap.cameraPosition.bearing.toString()
        val zoom = mapLibreMap.cameraPosition.zoom.toString()
        _uiState.value = uiState.value.copy(
            latitude = latitude,
            longitude = longitude,
            bearing = bearing,
            zoom = zoom
        )
    }

    /*
    Метод который вызывается по клику на карту.
    Рисует точки для и соединяющие их линии. Считает расстояние между двумя последними точками
    и длину всего пути
     */
    private fun onMapClick(
        point: LatLng,
    ): Boolean {
        distanceCalculator.addWaypoint(point)
        mapLibreMap.getStyle {
            val source = it.getSourceAs<GeoJsonSource>(GEO_JSON_SOURCE_ID)

            val mapPoints = distanceCalculator.getPoints().map { latLng ->
                Point.fromLngLat(
                    latLng.longitude,
                    latLng.latitude
                )
            }
            val featureList = buildList {
                mapPoints.forEach { point ->
                    add(Feature.fromGeometry(Point.fromLngLat(point.longitude(), point.latitude())))
                }
                add(Feature.fromGeometry(LineString.fromLngLats(mapPoints)))
            }

            val features = FeatureCollection.fromFeatures(featureList)
            source?.setGeoJson(features)

            if (it.getLayer(CIRCLE_LAYER_ID) == null && it.getLayer(LINE_LAYER_ID) == null) {
                if (it.getLayer(USER_LAYER_ID) == null) {
                    it.addLayerAbove(circleLayer, OSM_LAYER_ID)
                    it.addLayerBelow(lineLayer, CIRCLE_LAYER_ID)
                } else {
                    it.addLayerAbove(circleLayer, USER_LAYER_ID)
                    it.addLayerBelow(lineLayer, CIRCLE_LAYER_ID)
                }
            }
        }

        distanceCalculator.getDistance(DistanceUnits.KM)?.let {
            _uiState.value = uiState.value.copy(
                distanceBetweenTwoLast = String.format(
                    Locale.US,
                    "%.2f",
                    it.first
                ),
                totalDistance = String.format(Locale.US, "%.2f", it.second)
            )
        }
        return true
    }

    /*
    Метод который вызывается при нажатии кнопки списка на UI.
    Собирает все файлы с расширением mbtiles из папки Documents из external storage
    и отображает всплывающий диалог со списком этих файлов
     */
    private fun showLayersDialog() {
        val mbtileSources =
            tilesDir.list { _, name -> name.endsWith("mbtiles") }?.toList() ?: emptyList()
        _uiState.value =
            uiState.value.copy(isLayersDialogVisible = true, layersList = mbtileSources)
    }

    /*
    Прячет диалог с файлами mbtiles
     */
    private fun hideLayersDialog() {
        _uiState.value = uiState.value.copy(isLayersDialogVisible = false)
    }

    /*
    Метод который вызывается при изменении ползнука прозрачности слоя на UI
    Меняет прозрачность слоя c mbtiles
     */
    private fun changeLayerOpacity(newOpacity: Float) {
        userLayer?.let {
            it.setProperties(PropertyValue("raster-opacity", newOpacity))
            _uiState.value = uiState.value.copy(layerOpacity = newOpacity)
        }
    }

    /*
    Метод который вызывается когда пользователь нажимает кнопку Clear distance points
    Сбрасывает состояние калькулятора дистанции.
    Удаляет векторные слои на которых рисуется считаемый путь
     */
    private fun clearPoints() {
        distanceCalculator.reset()
        mapLibreMap.getStyle {
            it.removeLayer(circleLayer)
            it.removeLayer(lineLayer)
        }
        _uiState.value = uiState.value.copy(distanceBetweenTwoLast = "-", totalDistance = "-")
    }

    /*
    Метод который вызывается когда пользователь нажимает на кнопку Hide compass
    Изменяет видимость компаса на карте
     */
    private fun handleCompassVisibility() {
        mapLibreMap.uiSettings.isCompassEnabled = !mapLibreMap.uiSettings.isCompassEnabled
        _uiState.value =
            uiState.value.copy(isCompassVisible = mapLibreMap.uiSettings.isCompassEnabled)
    }

    /*
    Метод который вызывается когда пользователь нажимает на кнопку Hide scalebar
    Изменяет видимость масштабной линейки на карте
     */
    private fun handleScaleBarVisibility() {
        scaleBarPlugin.isEnabled = !scaleBarPlugin.isEnabled
        _uiState.value =
            uiState.value.copy(isScaleBarVisible = scaleBarPlugin.isEnabled)
    }

    /*
    Метод который вызывается когда пользователь нажимает на кнопку Reset camera
    Сбрасывает состояние камеры
     */
    private fun resetCamera() {
        mapLibreMap.animateCamera(object : CameraUpdate {
            override fun getCameraPosition(maplibreMap: MapLibreMap): CameraPosition {
                return CameraPosition.Builder()
                    .target(LatLng())
                    .bearing(0.0)
                    .zoom(0.0)
                    .build()
            }
        })
    }
}
