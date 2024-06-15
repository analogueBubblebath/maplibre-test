package ml.bubblebath.maplibre_test.ui.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ml.bubblebath.maplibre_test.ui.theme.MapButtonColor

@Composable
fun OnMapButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MapButtonColor),
        onClick = onClick
    ) {
        content()
    }
}