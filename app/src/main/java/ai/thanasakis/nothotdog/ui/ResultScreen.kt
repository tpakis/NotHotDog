package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.ui.theme.HotdogGreen
import ai.thanasakis.nothotdog.ui.theme.NotHotdogRed
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min

// Proportions measured from the show's screenshots, relative to the screen's shorter side.
private const val BANNER_HEIGHT = 0.27f
private const val BADGE_RADIUS = 0.1875f
private const val BADGE_OFFSET = 0.06f
private const val EDGE_WIDTH = 0.006f

@Composable
fun ResultScreen(photo: ImageBitmap, isHotdog: Boolean, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = stringResource(R.string.start_over),
                onClick = onDismiss,
            ),
    ) {
        val unit = min(maxWidth, maxHeight)
        val inset = (if (isHotdog) WindowInsets.statusBars else WindowInsets.navigationBars).asPaddingValues()
        val insetHeight = inset.calculateTopPadding() + inset.calculateBottomPadding()
        val bannerHeight = unit * BANNER_HEIGHT + insetHeight

        Image(
            bitmap = photo,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Banner(isHotdog, bannerHeight, Modifier.fillMaxSize())
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(if (isHotdog) Alignment.TopCenter else Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bannerHeight)
                .padding(inset),
        ) {
            OutlinedText(
                text = stringResource(if (isHotdog) R.string.hotdog else R.string.not_hotdog),
                fontSize = with(LocalDensity.current) { (unit * 0.135f).toSp() },
            )
        }
    }
}

/** The colored bar and the half-circle badge growing out of it, with a thin white edge. */
@Composable
private fun Banner(isHotdog: Boolean, bannerHeight: Dp, modifier: Modifier = Modifier) {
    val heightPx = with(LocalDensity.current) { bannerHeight.toPx() }
    Canvas(modifier) {
        val unit = minOf(size.width, size.height)
        val edge = unit * EDGE_WIDTH
        val radius = unit * BADGE_RADIUS
        val offset = unit * BADGE_OFFSET
        // The bar overshoots the screen so that only its inner side shows the white edge.
        val bar = if (isHotdog) {
            Rect(-edge, -edge, size.width + edge, heightPx)
        } else {
            Rect(-edge, size.height - heightPx, size.width + edge, size.height + edge)
        }
        val badgeCenter = Offset(
            x = size.width / 2,
            y = if (isHotdog) heightPx + offset else size.height - heightPx - offset,
        )
        val shape = Path.combine(
            PathOperation.Union,
            Path().apply { addRect(bar) },
            Path().apply { addOval(Rect(badgeCenter, radius)) },
        )
        drawPath(shape, if (isHotdog) HotdogGreen else NotHotdogRed)
        drawPath(shape, Color.White, style = Stroke(width = edge))
        if (isHotdog) drawCheck(badgeCenter, radius) else drawCross(badgeCenter, radius)
    }
}

private fun DrawScope.drawCheck(center: Offset, radius: Float) {
    val check = Path().apply {
        moveTo(center.x - 0.70f * radius, center.y + 0.15f * radius)
        lineTo(center.x - 0.26f * radius, center.y + 0.59f * radius)
        lineTo(center.x + 0.70f * radius, center.y - 0.37f * radius)
    }
    drawGlyph(check, radius)
}

private fun DrawScope.drawCross(center: Offset, radius: Float) {
    val arm = 0.48f * radius
    val crossCenter = center - Offset(0f, 0.18f * radius)
    val cross = Path().apply {
        moveTo(crossCenter.x - arm, crossCenter.y - arm)
        lineTo(crossCenter.x + arm, crossCenter.y + arm)
        moveTo(crossCenter.x + arm, crossCenter.y - arm)
        lineTo(crossCenter.x - arm, crossCenter.y + arm)
    }
    drawGlyph(cross, radius)
}

private fun DrawScope.drawGlyph(glyph: Path, radius: Float) {
    drawPath(
        glyph,
        Color.Black,
        style = Stroke(width = 0.22f * radius, cap = StrokeCap.Square, join = StrokeJoin.Miter),
    )
    drawPath(
        glyph,
        Color.White,
        style = Stroke(width = 0.13f * radius, cap = StrokeCap.Square, join = StrokeJoin.Miter),
    )
}
