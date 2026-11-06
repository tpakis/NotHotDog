package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.ui.theme.NunitoBlack
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/** White heavy lettering with a black outline, as on the app's banners. */
@Composable
fun OutlinedText(text: String, fontSize: TextUnit, modifier: Modifier = Modifier) {
    val style = TextStyle(fontFamily = NunitoBlack, fontSize = fontSize, lineHeight = fontSize * 1.2f)
    val fontSizePx = with(LocalDensity.current) { fontSize.toPx() }
    Box(modifier) {
        Text(
            text = text,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            color = Color.Black,
            // Only the fill below is exposed, so that accessibility services read the text once.
            modifier = Modifier.clearAndSetSemantics {},
            style = style.copy(
                drawStyle = Stroke(width = fontSizePx * 0.13f, join = StrokeJoin.Round),
                shadow = Shadow(Color.Black, Offset(0f, fontSizePx * 0.03f), blurRadius = fontSizePx * 0.05f),
            ),
        )
        Text(
            text = text,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            color = Color.White,
            style = style,
        )
    }
}
