package ml.bubblebath.maplibre_test.composable

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MbTileSourceItem(modifier: Modifier = Modifier, path: String, onClick: (String) -> Unit) {
    Text(
        modifier = modifier.clickable { onClick(path) },
        text = path
    )
}