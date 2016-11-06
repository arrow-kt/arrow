
import org.funktionale.either.Disjunction.*
import org.funktionale.validation.Validation
import org.funktionale.validation.validate
import org.testng.Assert.*
import org.testng.annotations.Test

data class ExampleForValidation(val number: Int, val text: String)

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

    @Test
    fun validate2Test() {
        val r1 = Right<String, Int>(1)
        val r2 = Right<String, String>("blahblah")
        val l1 = Left<String, Int>("fail1")
        val l2 = Left<String, String>("fail2")
        assertEquals(
                validate(r1, r2, ::ExampleForValidation),
                Right<List<String>, ExampleForValidation>(ExampleForValidation(1, "blahblah"))
        )
        assertEquals(
                validate(r1, l2, ::ExampleForValidation),
                Left<List<String>, ExampleForValidation>(listOf("fail2"))
        )
        assertEquals(
                validate(l1, l2, ::ExampleForValidation),
                Left<List<String>, ExampleForValidation>(listOf("fail1", "fail2"))
        )
    }
}