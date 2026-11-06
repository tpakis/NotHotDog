package ai.thanasakis.nothotdog.ui

import ai.thanasakis.nothotdog.R
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EvaluationScreensTest {
    @get:Rule
    val rule = createComposeRule()

    private val photo = ImageBitmap(100, 200)

    @Test
    fun evaluatingScreenShowsItsText() {
        rule.setContent { EvaluatingScreen(photo) }
        rule.onNodeWithText(string(R.string.evaluating)).assertIsDisplayed()
    }

    @Test
    fun hotdogResultShowsOnlyHotdog() {
        rule.setContent { ResultScreen(photo, isHotdog = true, onDismiss = {}) }
        rule.onNodeWithText(string(R.string.hotdog)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.not_hotdog)).assertDoesNotExist()
    }

    @Test
    fun notHotdogResultShowsOnlyNotHotdog() {
        rule.setContent { ResultScreen(photo, isHotdog = false, onDismiss = {}) }
        rule.onNodeWithText(string(R.string.not_hotdog)).assertIsDisplayed()
        rule.onNodeWithText(string(R.string.hotdog)).assertDoesNotExist()
    }

    @Test
    fun tappingAResultDismissesIt() {
        var dismissals = 0
        rule.setContent { ResultScreen(photo, isHotdog = true, onDismiss = { dismissals++ }) }
        rule.onNodeWithText(string(R.string.hotdog)).performClick()
        assertEquals(1, dismissals)
    }
}
