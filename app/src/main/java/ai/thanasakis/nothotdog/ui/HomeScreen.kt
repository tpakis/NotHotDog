package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.classifier.ModelState
import ai.thanasakis.nothotdog.ui.theme.HomeBackground
import ai.thanasakis.nothotdog.ui.theme.HotdogGreen
import ai.thanasakis.nothotdog.ui.theme.NotHotdogRed
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    model: ModelState,
    @StringRes message: Int?,
    onTakePhoto: () -> Unit,
    onChoosePhoto: () -> Unit,
    onCheckAgain: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ready = model == ModelState.Ready
    var showShareDialog by rememberSaveable { mutableStateOf(false) }
    BoxWithConstraints(modifier.fillMaxSize().background(HomeBackground).safeDrawingPadding()) {
        val unit = min(maxWidth, maxHeight)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
        ) {
            OutlinedText(
                text = stringResource(R.string.app_name),
                fontSize = with(LocalDensity.current) { (unit * 0.125f).toSp() },
            )
            Spacer(Modifier.height(48.dp))
            HomeButton(R.string.take_photo, R.drawable.ic_camera, HotdogGreen, ready, onTakePhoto)
            Spacer(Modifier.height(20.dp))
            HomeButton(R.string.choose_photo, R.drawable.ic_image, NotHotdogRed, ready, onChoosePhoto)
            Spacer(Modifier.height(24.dp))
            Box(Modifier.height(96.dp), contentAlignment = Alignment.TopCenter) {
                ModelStatus(model, message, onCheckAgain)
            }
        }
        Row(Modifier.align(Alignment.TopEnd).padding(8.dp)) {
            IconButton(onClick = onOpenSettings) {
                Icon(painterResource(R.drawable.ic_settings), stringResource(R.string.settings), tint = Color.White)
            }
            IconButton(onClick = { showShareDialog = true }) {
                Icon(painterResource(R.drawable.ic_share), stringResource(R.string.share), tint = Color.White)
            }
        }
    }
    if (showShareDialog) {
        ShareDialog(onDismiss = { showShareDialog = false })
    }
}

@Composable
private fun HomeButton(
    @StringRes label: Int,
    @DrawableRes icon: Int,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        color = color,
        border = BorderStroke(2.dp, Color.White),
        modifier = Modifier.fillMaxWidth().height(72.dp).alpha(if (enabled) 1f else 0.4f),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(icon), contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            OutlinedText(text = stringResource(label), fontSize = 26.sp)
        }
    }
}

@Composable
private fun ModelStatus(model: ModelState, @StringRes message: Int?, onCheckAgain: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val status = message?.let { stringResource(it) } ?: when (model) {
            ModelState.Checking -> stringResource(R.string.model_checking)
            is ModelState.Downloading -> model.progress?.let {
                stringResource(R.string.model_downloading_percent, (it * 100).roundToInt())
            } ?: stringResource(R.string.model_downloading)
            ModelState.Unavailable -> stringResource(R.string.model_unavailable)
            ModelState.Ready -> null
        }
        status?.let { Text(it, color = Color.White, textAlign = TextAlign.Center) }
        if (model == ModelState.Unavailable) {
            TextButton(onClick = onCheckAgain) {
                Text(stringResource(R.string.check_again), color = Color.White)
            }
        }
    }
}
