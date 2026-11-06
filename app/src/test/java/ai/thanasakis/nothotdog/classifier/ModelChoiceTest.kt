package ai.thanasakis.nothotdog.classifier

import com.google.mlkit.genai.prompt.ModelPreference
import com.google.mlkit.genai.prompt.ModelReleaseStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelChoiceTest {
    @Test
    fun allCoversEveryCombinationOnce() {
        assertEquals(4, ModelChoice.ALL.size)
        assertEquals(4, ModelChoice.ALL.toSet().size)
    }

    @Test
    fun defaultIsStableAndFull() {
        assertEquals(ModelChoice(ModelReleaseStage.STABLE, ModelPreference.FULL), ModelChoice.DEFAULT)
        assertTrue(ModelChoice.DEFAULT in ModelChoice.ALL)
    }

    @Test
    fun flagsFollowTheStageAndPreference() {
        val previewFast = ModelChoice(ModelReleaseStage.PREVIEW, ModelPreference.FAST)
        assertTrue(previewFast.isPreview)
        assertTrue(previewFast.isFast)
        assertFalse(ModelChoice.DEFAULT.isPreview)
        assertFalse(ModelChoice.DEFAULT.isFast)
    }
}
