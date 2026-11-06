package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.classifier.ModelState
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview

private fun samplePhoto(): ImageBitmap {
    val bitmap = Bitmap.createBitmap(600, 1200, Bitmap.Config.ARGB_8888)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    Canvas(bitmap).apply {
        drawColor(0xFF4A2C20.toInt())
        paint.color = 0xFFF2DDB0.toInt()
        drawRoundRect(40f, 480f, 560f, 760f, 120f, 120f, paint)
        paint.color = 0xFFC8323A.toInt()
        drawRoundRect(20f, 570f, 580f, 670f, 50f, 50f, paint)
    }
    return bitmap.asImageBitmap()
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HomeReadyPreview() {
    HomeScreen(ModelState.Ready, null, {}, {}, {})
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HomeUnavailablePreview() {
    HomeScreen(ModelState.Unavailable, null, {}, {}, {})
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun EvaluatingPreview() {
    EvaluatingScreen(samplePhoto())
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HotdogPreview() {
    ResultScreen(samplePhoto(), isHotdog = true, onDismiss = {})
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun NotHotdogPreview() {
    ResultScreen(samplePhoto(), isHotdog = false, onDismiss = {})
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun ShareDialogPreview() {
    ShareDialogContent(onDismiss = {})
}
