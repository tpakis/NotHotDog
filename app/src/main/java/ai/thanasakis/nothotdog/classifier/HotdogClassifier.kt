package ai.thanasakis.nothotdog.classifier

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.mlkit.genai.common.DownloadStatus
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.ImagePart
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.Closeable
import java.io.IOException
import kotlin.math.max
import kotlin.math.roundToInt

private const val TAG = "HotdogClassifier"
private const val MAX_INFERENCE_SIDE = 1024

private const val PROMPT = """Does this photo show a hot dog, meaning a sausage served in a sliced bun?
##
Answer with one word: YES or NO."""

sealed interface ModelState {
    data object Checking : ModelState
    data class Downloading(val progress: Float?) : ModelState
    data object Ready : ModelState
    data object Unavailable : ModelState
}

/** Answers "is it a hotdog?" with Gemini Nano, which runs on the device through AICore. */
class HotdogClassifier(choice: ModelChoice = ModelChoice.DEFAULT) : Closeable {
    private val model = choice.createClient()

    /** Reports whether this device can run the model, downloading the model if it needs to. */
    fun prepare(): Flow<ModelState> = flow {
        emit(ModelState.Checking)
        when (model.checkStatus()) {
            FeatureStatus.AVAILABLE -> emit(ModelState.Ready)
            FeatureStatus.DOWNLOADABLE, FeatureStatus.DOWNLOADING -> {
                emit(ModelState.Downloading(progress = null))
                var bytesToDownload = 0L
                model.download().collect { status ->
                    when (status) {
                        is DownloadStatus.DownloadStarted -> bytesToDownload = status.bytesToDownload
                        is DownloadStatus.DownloadProgress -> emit(
                            ModelState.Downloading(
                                progress = if (bytesToDownload > 0) {
                                    status.totalBytesDownloaded.toFloat() / bytesToDownload
                                } else {
                                    null
                                },
                            ),
                        )
                        DownloadStatus.DownloadCompleted -> emit(ModelState.Ready)
                        is DownloadStatus.DownloadFailed -> throw status.e
                    }
                }
            }
            else -> emit(ModelState.Unavailable)
        }
    }.catch { e ->
        Log.w(TAG, "Gemini Nano is not usable on this device", e)
        emit(ModelState.Unavailable)
    }

    suspend fun isHotdog(photo: Bitmap, maxOutputTokens: Int): Boolean {
        val request = generateContentRequest(ImagePart(photo.fitInto(MAX_INFERENCE_SIDE)), TextPart(PROMPT)) {
            temperature = 0f
            topK = 1
            this.maxOutputTokens = maxOutputTokens
        }
        val answer = model.generateContent(request).candidates.firstOrNull()?.text
        return parseAnswer(answer) ?: throw IOException("Unexpected model answer: $answer")
    }

    override fun close() = model.close()
}

internal fun parseAnswer(answer: String?): Boolean? {
    val word = answer?.let { Regex("""^\W*(yes|no)\b""", RegexOption.IGNORE_CASE).find(it) }
    return word?.groupValues?.get(1)?.equals("yes", ignoreCase = true)
}

private fun Bitmap.fitInto(maxSide: Int): Bitmap {
    val longestSide = max(width, height)
    if (longestSide <= maxSide) return this
    val ratio = maxSide.toFloat() / longestSide
    return scale((width * ratio).roundToInt(), (height * ratio).roundToInt())
}
