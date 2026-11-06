package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import ai.thanasakis.nothotdog.classifier.ModelState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val rule = createComposeRule()

    private var takePhotoClicks = 0
    private var choosePhotoClicks = 0
    private var checkAgainClicks = 0
    private var settingsClicks = 0

    private fun setHome(model: ModelState, message: Int? = null) {
        rule.setContent {
            HomeScreen(
                model = model,
                message = message,
                onTakePhoto = { takePhotoClicks++ },
                onChoosePhoto = { choosePhotoClicks++ },
                onCheckAgain = { checkAgainClicks++ },
                onOpenSettings = { settingsClicks++ },
            )
        }
    }

    @Test
    fun showsTheTitle() {
        setHome(ModelState.Ready)
        rule.onNodeWithText(string(R.string.app_name)).assertIsDisplayed()
    }

    @Test
    fun photoButtonsWorkWhenTheModelIsReady() {
        setHome(ModelState.Ready)
        rule.onNodeWithText(string(R.string.take_photo)).assertIsEnabled().performClick()
        rule.onNodeWithText(string(R.string.choose_photo)).assertIsEnabled().performClick()
        assertEquals(1, takePhotoClicks)
        assertEquals(1, choosePhotoClicks)
    }

    @Test
    fun photoButtonsAreDisabledUntilTheModelIsReady() {
        setHome(ModelState.Checking)
        rule.onNodeWithText(string(R.string.take_photo)).assertIsNotEnabled().performClick()
        rule.onNodeWithText(string(R.string.choose_photo)).assertIsNotEnabled().performClick()
        assertEquals(0, takePhotoClicks)
        assertEquals(0, choosePhotoClicks)
        rule.onNodeWithText(string(R.string.model_checking)).assertIsDisplayed()
    }

    @Test
    fun showsDownloadProgress() {
        setHome(ModelState.Downloading(0.42f))
        rule.onNodeWithText(string(R.string.model_downloading_percent, 42)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.take_photo)).assertIsNotEnabled()
    }

    @Test
    fun showsDownloadWithoutAKnownSize() {
        setHome(ModelState.Downloading(null))
        rule.onNodeWithText(string(R.string.model_downloading)).assertIsDisplayed()
    }

    @Test
    fun unavailableModelOffersCheckAgain() {
        setHome(ModelState.Unavailable)
        rule.onNodeWithText(string(R.string.model_unavailable)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.check_again)).performClick()
        assertEquals(1, checkAgainClicks)
    }

    @Test
    fun readyModelDoesNotOfferCheckAgain() {
        setHome(ModelState.Ready)
        rule.onNodeWithText(string(R.string.check_again)).assertDoesNotExist()
    }

    @Test
    fun messageReplacesTheModelStatus() {
        setHome(ModelState.Unavailable, message = R.string.evaluation_failed)
        rule.onNodeWithText(string(R.string.evaluation_failed)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.model_unavailable)).assertDoesNotExist()
    }

    @Test
    fun settingsButtonWorksEvenWhenTheModelIsUnavailable() {
        setHome(ModelState.Unavailable)
        rule.onNodeWithContentDescription(string(R.string.settings)).performClick()
        assertEquals(1, settingsClicks)
    }

    @Test
    fun shareButtonOpensAndClosesTheDialog() {
        setHome(ModelState.Unavailable)
        rule.onNodeWithText(string(R.string.share_url)).assertDoesNotExist()

        rule.onNodeWithContentDescription(string(R.string.share)).performClick()
        rule.onNodeWithText(string(R.string.share_url)).assertIsDisplayed()
        rule.onNodeWithContentDescription(string(R.string.share_qr_description)).assertIsDisplayed()

        rule.onNodeWithText(string(R.string.close)).performClick()
        rule.onNodeWithText(string(R.string.share_url)).assertDoesNotExist()
    }
}
