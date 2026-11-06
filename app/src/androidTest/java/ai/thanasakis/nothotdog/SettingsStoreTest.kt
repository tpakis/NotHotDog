package ai.thanasakis.nothotdog

import ai.thanasakis.nothotdog.classifier.ModelChoice
import android.content.Context
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsStoreTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun clearSettings() {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit(commit = true) { clear() }
    }

    @Test
    fun startsWithDefaults() {
        val store = SettingsStore(context)
        assertEquals(ModelChoice.DEFAULT, store.modelChoice)
        assertEquals(DEFAULT_MAX_OUTPUT_TOKENS, store.maxOutputTokens)
    }

    @Test
    fun valuesSurviveANewInstance() {
        val choice = ModelChoice.ALL.last()
        SettingsStore(context).apply {
            modelChoice = choice
            maxOutputTokens = 64
        }
        val reopened = SettingsStore(context)
        assertEquals(choice, reopened.modelChoice)
        assertEquals(64, reopened.maxOutputTokens)
    }
}
