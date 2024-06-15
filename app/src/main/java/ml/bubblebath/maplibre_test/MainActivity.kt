package ml.bubblebath.maplibre_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ml.bubblebath.maplibre_test.navigation.NavRoutes
import ml.bubblebath.maplibre_test.navigation.buildNavGraph
import ml.bubblebath.maplibre_test.ui.theme.MaplibretestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaplibretestTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.MainScreen.uri
                ) {
                    buildNavGraph()
                }
            }
        }
    }
}
