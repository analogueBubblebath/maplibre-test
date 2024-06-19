package ml.bubblebath.maplibre_test.model

import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.PropertyValue


fun Layer.show() = setProperties(PropertyValue("visibility", "visible"))
fun Layer.hide() = setProperties(PropertyValue("visibility", "none"))
