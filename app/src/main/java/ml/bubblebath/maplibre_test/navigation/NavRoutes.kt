package ml.bubblebath.maplibre_test.navigation

sealed class NavRoutes(val uri: String) {
    data object MainScreen : NavRoutes(uri = "MainScreen")
}