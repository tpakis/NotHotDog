package ai.thanasakis.nothotdog.classifier

import org.junit.Assert.assertEquals
import org.junit.Test

class ParseAnswerTest {
    @Test
    fun yesAnswersAreHotdogs() {
        listOf("YES", "yes", "Yes.", " yes\n", "**YES**").forEach { assertEquals(it, true, parseAnswer(it)) }
    }

    @Test
    fun noAnswersAreNotHotdogs() {
        listOf("NO", "no", "No.", " no\n", "**NO**").forEach { assertEquals(it, false, parseAnswer(it)) }
    }

    @Test
    fun anythingElseIsNotAnAnswer() {
        listOf(null, "", "maybe", "Not sure", "nope", "yesterday").forEach { assertEquals(it, null, parseAnswer(it)) }
    }
}
