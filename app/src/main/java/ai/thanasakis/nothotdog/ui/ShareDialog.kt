package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.ui.theme.HomeBackground
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun ShareDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        ShareDialogContent(onDismiss)
    }
}

@Composable
internal fun ShareDialogContent(onDismiss: () -> Unit) {
    val url = stringResource(R.string.share_url)
    val qrCode = remember(url) { qrCode(url) }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = HomeBackground,
        border = BorderStroke(2.dp, Color.White),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            OutlinedText(text = stringResource(R.string.share), fontSize = 32.sp)
            Image(
                bitmap = qrCode,
                contentDescription = stringResource(R.string.share_qr_description),
                // Nearest-neighbour scaling keeps the modules sharp, so that the code stays scannable.
                filterQuality = FilterQuality.None,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Text(
                text = buildAnnotatedString {
                    withLink(
                        LinkAnnotation.Url(
                            url,
                            TextLinkStyles(SpanStyle(color = Color.White, textDecoration = TextDecoration.Underline)),
                        ),
                    ) {
                        append(url)
                    }
                },
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close), color = Color.White)
            }
        }
    }
}

/** Draws the code at one pixel per module, including the quiet zone that scanners need around it. */
private fun qrCode(content: String): ImageBitmap {
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, 0, 0)
    val bitmap = createBitmap(matrix.width, matrix.height)
    for (x in 0 until matrix.width) {
        for (y in 0 until matrix.height) {
            bitmap[x, y] = if (matrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }
    return bitmap.asImageBitmap()
}
