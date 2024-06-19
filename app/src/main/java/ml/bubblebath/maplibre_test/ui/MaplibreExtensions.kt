package ml.bubblebath.maplibre_test.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyValue


fun Layer.show() = setProperties(PropertyValue("visibility", "visible"))
fun Layer.hide() = setProperties(PropertyValue("visibility", "none"))

fun LineLayer.setLineColor(color: Color) {
    val argbValue = color.toArgb()
    val hexColorString = String.format("#%06X", 0xFFFFFF and argbValue)
    setProperties(PropertyValue("line-color", hexColorString))
}
