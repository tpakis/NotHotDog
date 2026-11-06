package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.MainActivity
import ai.thanasakis.nothotdog.R
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.provider.MediaStore
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val MODEL_TIMEOUT_MS = 20_000L

/** Runs the real app. Tests that need the model are skipped on devices without Gemini Nano. */
class MainActivityTest {
    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Before
    fun stubExternalActivities() {
        Intents.init()
        // Answers every external activity at once, so that no real camera or photo picker opens.
        intending(not(isInternal())).respondWith(ActivityResult(Activity.RESULT_CANCELED, null))
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    private fun assumeModelReady() {
        val enabledButton = hasText(string(R.string.take_photo)) and
            SemanticsMatcher.keyNotDefined(SemanticsProperties.Disabled)
        val ready = runCatching {
            rule.waitUntil(MODEL_TIMEOUT_MS) { rule.onAllNodes(enabledButton).fetchSemanticsNodes().isNotEmpty() }
        }.isSuccess
        assumeTrue("Gemini Nano isn't ready on this device", ready)
    }

    @Test
    fun startsOnTheHomeScreen() {
        rule.onNodeWithText(string(R.string.app_name)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.take_photo)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.choose_photo)).assertIsDisplayed()
    }

    @Test
    fun takePhotoOpensTheCamera() {
        assumeModelReady()
        rule.onNodeWithText(string(R.string.take_photo)).performClick()
        rule.waitForIdle()
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE))
        rule.onNodeWithText(string(R.string.app_name)).assertIsDisplayed()
    }

    @Test
    fun choosePhotoOpensThePicker() {
        assumeModelReady()
        rule.onNodeWithText(string(R.string.choose_photo)).performClick()
        rule.waitForIdle()
        intended(hasAction(MediaStore.ACTION_PICK_IMAGES))
        rule.onNodeWithText(string(R.string.app_name)).assertIsDisplayed()
    }

    @Test
    fun settingsListsTheModels() {
        rule.onNodeWithContentDescription(string(R.string.settings)).performClick()
        rule.waitUntil(MODEL_TIMEOUT_MS) {
            rule.onAllNodes(hasText(string(R.string.settings_checking_models))).fetchSemanticsNodes().isEmpty()
        }
        rule.onNodeWithText(string(R.string.model_stable_full)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.model_preview_fast)).performScrollTo().assertIsDisplayed()

        rule.onNodeWithText(string(R.string.close)).performScrollTo().performClick()
        rule.onNodeWithText(string(R.string.settings_model)).assertDoesNotExist()
    }

    @Test
    fun shareShowsTheLink() {
        rule.onNodeWithContentDescription(string(R.string.share)).performClick()
        rule.onNodeWithText(string(R.string.share_url)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.close)).performClick()
        rule.onNodeWithText(string(R.string.share_url)).assertDoesNotExist()
    }
}
