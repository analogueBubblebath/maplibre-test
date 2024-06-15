package ml.bubblebath.maplibre_test.model

enum class TileSetScheme(private val code: String) {
    XYZ("xyz"),
    TMS("tms");

    override fun toString(): String = code
}