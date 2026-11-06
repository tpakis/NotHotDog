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
        listOf(null, "", "   ", "maybe", "Not sure", "nope", "yesterday", "nothing", "**").forEach {
            assertEquals(it, null, parseAnswer(it))
        }
    }

    @Test
    fun onlyTheFirstWordCounts() {
        assertEquals(true, parseAnswer("Yes, it is a hot dog"))
        assertEquals(false, parseAnswer("No, that is a pizza"))
        assertEquals(false, parseAnswer("NO yes"))
        assertEquals(null, parseAnswer("The answer is yes"))
    }
}
