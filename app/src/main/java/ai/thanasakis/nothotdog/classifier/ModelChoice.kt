package ai.thanasakis.nothotdog.classifier

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.ModelConfig
import com.google.mlkit.genai.prompt.ModelPreference
import com.google.mlkit.genai.prompt.ModelReleaseStage
import com.google.mlkit.genai.prompt.generationConfig
import com.google.mlkit.genai.prompt.modelConfig
import kotlinx.coroutines.CancellationException

/** One of the model variants that apps can ask AICore for. */
data class ModelChoice(val releaseStage: Int, val preference: Int) {
    val isPreview get() = releaseStage == ModelReleaseStage.PREVIEW
    val isFast get() = preference == ModelPreference.FAST

    fun createClient(): GenerativeModel = Generation.getClient(
        generationConfig {
            modelConfig = modelConfig {
                releaseStage = this@ModelChoice.releaseStage
                preference = this@ModelChoice.preference
            }
        },
    )

    companion object {
        val DEFAULT = ModelConfig.DEFAULT.let { ModelChoice(it.releaseStage, it.preference) }
        val ALL = listOf(ModelReleaseStage.STABLE, ModelReleaseStage.PREVIEW).flatMap { stage ->
            listOf(ModelPreference.FULL, ModelPreference.FAST).map { ModelChoice(stage, it) }
        }
    }
}

enum class ModelAvailability { INSTALLED, DOWNLOADABLE, DOWNLOADING, UNAVAILABLE }

data class ModelChoiceInfo(
    val choice: ModelChoice,
    val availability: ModelAvailability,
    val name: String? = null,
    val tokenLimit: Int? = null,
)

/** The API can't list models, so this asks AICore about each variant in turn. */
suspend fun probeModelChoices(): List<ModelChoiceInfo> = ModelChoice.ALL.map { choice ->
    val client = choice.createClient()
    try {
        val availability = when (client.checkStatus()) {
            FeatureStatus.AVAILABLE -> ModelAvailability.INSTALLED
            FeatureStatus.DOWNLOADABLE -> ModelAvailability.DOWNLOADABLE
            FeatureStatus.DOWNLOADING -> ModelAvailability.DOWNLOADING
            else -> ModelAvailability.UNAVAILABLE
        }
        if (availability == ModelAvailability.INSTALLED) {
            ModelChoiceInfo(choice, availability, client.getBaseModelName(), client.getTokenLimit())
        } else {
            ModelChoiceInfo(choice, availability)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        ModelChoiceInfo(choice, ModelAvailability.UNAVAILABLE)
    } finally {
        client.close()
    }
}
