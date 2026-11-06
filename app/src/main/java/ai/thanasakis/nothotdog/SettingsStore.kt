package ai.thanasakis.nothotdog

import ai.thanasakis.nothotdog.classifier.ModelChoice
import android.content.Context
import androidx.core.content.edit

const val DEFAULT_MAX_OUTPUT_TOKENS = 3

class SettingsStore(context: Context) {
    private val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var modelChoice: ModelChoice
        get() = ModelChoice(
            releaseStage = preferences.getInt(KEY_RELEASE_STAGE, ModelChoice.DEFAULT.releaseStage),
            preference = preferences.getInt(KEY_PREFERENCE, ModelChoice.DEFAULT.preference),
        )
        set(value) = preferences.edit {
            putInt(KEY_RELEASE_STAGE, value.releaseStage)
            putInt(KEY_PREFERENCE, value.preference)
        }

    var maxOutputTokens: Int
        get() = preferences.getInt(KEY_MAX_OUTPUT_TOKENS, DEFAULT_MAX_OUTPUT_TOKENS)
        set(value) = preferences.edit { putInt(KEY_MAX_OUTPUT_TOKENS, value) }

    private companion object {
        const val KEY_RELEASE_STAGE = "release_stage"
        const val KEY_PREFERENCE = "preference"
        const val KEY_MAX_OUTPUT_TOKENS = "max_output_tokens"
    }
}
