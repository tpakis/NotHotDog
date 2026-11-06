package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.classifier.ModelAvailability
import ai.thanasakis.nothotdog.classifier.ModelChoice
import ai.thanasakis.nothotdog.classifier.ModelChoiceInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class SettingsDialogTest {
    @get:Rule
    val rule = createComposeRule()

    private val stableFull = ModelChoice.ALL[0]
    private val stableFast = ModelChoice.ALL[1]
    private val previewFull = ModelChoice.ALL[2]
    private val previewFast = ModelChoice.ALL[3]

    private val choices = listOf(
        ModelChoiceInfo(stableFull, ModelAvailability.INSTALLED, "nano-v3", 32768),
        ModelChoiceInfo(stableFast, ModelAvailability.UNAVAILABLE),
        ModelChoiceInfo(previewFull, ModelAvailability.INSTALLED, "nano-v4-full", 32768),
        ModelChoiceInfo(previewFast, ModelAvailability.DOWNLOADABLE),
    )

    private var selectedModel: ModelChoice? = null
    private var selectedTokens: Int? = null
    private var dismissals = 0

    private fun setDialog(choices: List<ModelChoiceInfo>? = this.choices) {
        rule.setContent {
            SettingsDialogContent(
                choices = choices,
                selected = stableFull,
                maxOutputTokens = 3,
                onSelectModel = { selectedModel = it },
                onSelectMaxOutputTokens = { selectedTokens = it },
                onDismiss = { dismissals++ },
            )
        }
    }

    @Test
    fun showsAMessageWhileCheckingModels() {
        setDialog(choices = null)
        rule.onNodeWithText(string(R.string.settings_checking_models)).assertIsDisplayed()
    }

    @Test
    fun describesEachModel() {
        setDialog()
        rule.onNodeWithText(string(R.string.model_installed, "nano-v3", 32768)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.model_status_unavailable)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.model_downloadable)).assertIsDisplayed()
    }

    @Test
    fun marksTheSelectedModel() {
        setDialog()
        rule.onNodeWithText(string(R.string.model_stable_full)).assertIsSelected()
        rule.onNodeWithText(string(R.string.model_preview_full)).assertIsNotSelected()
    }

    @Test
    fun selectingAnInstalledModelReportsIt() {
        setDialog()
        rule.onNodeWithText(string(R.string.model_preview_full)).assertIsEnabled().performClick()
        assertEquals(previewFull, selectedModel)
    }

    @Test
    fun aDownloadableModelCanBeSelected() {
        setDialog()
        rule.onNodeWithText(string(R.string.model_preview_fast)).assertIsEnabled().performClick()
        assertEquals(previewFast, selectedModel)
    }

    @Test
    fun anUnavailableModelCannotBeSelected() {
        setDialog()
        rule.onNodeWithText(string(R.string.model_stable_fast)).assertIsNotEnabled().performClick()
        assertNull(selectedModel)
    }

    @Test
    fun selectingAnswerLengthReportsIt() {
        setDialog()
        rule.onNodeWithText("3").performScrollTo().assertIsSelected()
        rule.onNodeWithText("64").performScrollTo().assertIsNotSelected().performClick()
        assertEquals(64, selectedTokens)
    }

    @Test
    fun closeButtonDismisses() {
        setDialog()
        rule.onNodeWithText(string(R.string.close)).performScrollTo().performClick()
        assertEquals(1, dismissals)
    }
}
