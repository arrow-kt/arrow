
import org.funktionale.either.Disjunction.*
import org.funktionale.validation.Validation
import org.testng.Assert.*
import org.testng.annotations.Test

class ValidationTest {

    @Test
    fun validationTest() {
        val d1 = Right<String, Int>(1)
        val d2 = Right<String, Int>(2)
        val d3 = Right<String, Int>(3)

        val validation = Validation(d1, d2, d3)
        assertFalse(validation.hasFailures)
        assertEquals(validation.failures, listOf<String>())
    }

    @Test
    fun validationTestWithError() {
        val d1 = Right<String, Int>(1)
        val d2 = Left<String, Int>("Not a number")
        val d3 = Right<String, Int>(3)

        val validation = Validation(d1, d2, d3)
        assertTrue(validation.hasFailures)
        assertEquals(validation.failures, listOf("Not a number"))
    }
}