package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.NotHotDogViewModel
import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.Screen
import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File

@Composable
fun NotHotDogApp(viewModel: NotHotDogViewModel = viewModel()) {
    val context = LocalContext.current
    var pendingCapture by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showSettings by rememberSaveable { mutableStateOf(false) }

    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        pendingCapture?.takeIf { saved }?.let(viewModel::evaluate)
    }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::evaluate)
    }

    val state = viewModel.uiState
    BackHandler(enabled = state.screen != Screen.Home, onBack = viewModel::backToHome)

    // The result replaces the spinner with a hard cut, as it does in the show.
    when (val screen = state.screen) {
        Screen.Home -> HomeScreen(
            model = state.model,
            message = state.message,
            onTakePhoto = {
                val uri = newCaptureUri(context)
                pendingCapture = uri
                try {
                    camera.launch(uri)
                } catch (_: ActivityNotFoundException) {
                    viewModel.showMessage(R.string.no_camera_app)
                }
            },
            onChoosePhoto = {
                photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onCheckAgain = viewModel::checkModel,
            onOpenSettings = { showSettings = true },
        )
        is Screen.Evaluating -> EvaluatingScreen(screen.photo)
        is Screen.Result -> ResultScreen(screen.photo, screen.isHotdog, onDismiss = viewModel::backToHome)
    }

    if (showSettings) {
        // Runs on every open, and also after the dialog is restored without a ViewModel following process death.
        LaunchedEffect(Unit) { viewModel.refreshModelChoices() }
        SettingsDialog(
            choices = state.modelChoices,
            selected = state.selectedModel,
            maxOutputTokens = state.maxOutputTokens,
            onSelectModel = viewModel::selectModel,
            onSelectMaxOutputTokens = viewModel::selectMaxOutputTokens,
            onDismiss = { showSettings = false },
        )
    }
}

private fun newCaptureUri(context: Context): Uri {
    val directory = File(context.filesDir, "photos").apply { mkdirs() }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(directory, "capture.jpg"))
}
