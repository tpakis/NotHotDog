package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.ui.theme.SpinnerHead
import ai.thanasakis.nothotdog.ui.theme.SpinnerTail
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sin

private const val SPINNER_SLOTS = 12
private const val SPINNER_DOTS = 11
private const val SPINNER_PERIOD_MS = 1200
private const val SPINNER_CENTER_Y = 0.35f

@Composable
fun EvaluatingScreen(photo: ImageBitmap, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxSize().background(Color.Black)) {
        val unit = min(maxWidth, maxHeight)
        Image(
            bitmap = photo,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(4.dp),
        )
        Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.4f)))
        DotSpinner(Modifier.fillMaxSize())
        OutlinedText(
            text = stringResource(R.string.evaluating),
            fontSize = with(LocalDensity.current) { (unit * 0.135f).toSp() },
            modifier = Modifier.align(BiasAlignment(0f, 0.24f)),
        )
    }
}

/** A ring of dots growing towards a red head, which steps clockwise around the ring. */
@Composable
private fun DotSpinner(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = SPINNER_SLOTS.toFloat(),
        animationSpec = infiniteRepeatable(tween(SPINNER_PERIOD_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation",
    )
    Canvas(modifier) {
        val unit = min(size.width, size.height)
        val center = Offset(size.width / 2, size.height * SPINNER_CENTER_Y)
        val ringRadius = unit * 0.22f
        val head = floor(rotation)
        for (dot in 0 until SPINNER_DOTS) {
            val fraction = dot.toFloat() / (SPINNER_DOTS - 1)
            val angle = Math.toRadians((head - dot) * 360.0 / SPINNER_SLOTS - 90.0)
            val position = center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * ringRadius
            val radius = unit * (0.043f - 0.016f * fraction)
            drawCircle(lerp(SpinnerHead, SpinnerTail, fraction), radius, position)
            drawCircle(Color.Black, radius, position, style = Stroke(width = unit * 0.006f))
        }
    }
}
