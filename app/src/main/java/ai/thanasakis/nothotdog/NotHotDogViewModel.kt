package ai.thanasakis.nothotdog

import ai.thanasakis.nothotdog.classifier.HotdogClassifier
import ai.thanasakis.nothotdog.classifier.ModelState
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface Screen {
    data object Home : Screen
    data class Evaluating(val photo: ImageBitmap) : Screen
    data class Result(val photo: ImageBitmap, val isHotdog: Boolean) : Screen
}

data class UiState(
    val model: ModelState = ModelState.Checking,
    val screen: Screen = Screen.Home,
    @StringRes val message: Int? = null,
)

class NotHotDogViewModel(application: Application) : AndroidViewModel(application) {
    private val classifier = HotdogClassifier()
    private var evaluation: Job? = null

    var uiState by mutableStateOf(UiState())
        private set

    init {
        checkModel()
    }

    fun checkModel() {
        viewModelScope.launch {
            classifier.prepare().collect { uiState = uiState.copy(model = it) }
        }
    }

    fun evaluate(uri: Uri) {
        evaluation?.cancel()
        evaluation = viewModelScope.launch {
            try {
                val photo = withContext(Dispatchers.IO) {
                    loadPhoto(getApplication<Application>().contentResolver, uri)
                }
                val image = photo.asImageBitmap()
                uiState = uiState.copy(screen = Screen.Evaluating(image), message = null)
                val isHotdog = classifier.isHotdog(photo)
                uiState = uiState.copy(screen = Screen.Result(image, isHotdog))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Evaluation failed", e)
                uiState = uiState.copy(screen = Screen.Home, message = R.string.evaluation_failed)
            }
        }
    }

    fun showMessage(@StringRes message: Int) {
        uiState = uiState.copy(message = message)
    }

    fun backToHome() {
        evaluation?.cancel()
        uiState = uiState.copy(screen = Screen.Home)
    }

    override fun onCleared() {
        classifier.close()
    }

    private companion object {
        const val TAG = "NotHotDogViewModel"
    }
}
