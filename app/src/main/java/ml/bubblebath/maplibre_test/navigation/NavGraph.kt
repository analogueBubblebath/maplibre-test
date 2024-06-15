package ml.bubblebath.maplibre_test.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import ml.bubblebath.maplibre_test.ui.screens.main.MainScreen

fun NavGraphBuilder.buildNavGraph() {
    composable(NavRoutes.MainScreen.uri) {
        MainScreen(modifier = Modifier.fillMaxSize())
    }
}