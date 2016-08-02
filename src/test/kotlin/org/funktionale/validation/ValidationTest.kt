import org.funktionale.either.Either
import org.funktionale.validation.Validation
import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.Assert.assertFalse
import org.testng.annotations.Test

class ValidationTest {

    @Test
    fun validationTest() {
        val e1: Either<String, Int> = Either.Right(1)
        val e2: Either<String, Int> = Either.Right(2)
        val e3: Either<String, Int> = Either.Right(3)

        val validation = Validation(e1, e2, e3)
        assertFalse(validation.hasFailures)
        assertEquals(validation.failures, listOf<String>())
    }

    @Test
    fun validationTestWithError() {
        val e1: Either<String, Int> = Either.Right(1)
        val e2: Either<String, Int> = Either.Left("Not a number")
        val e3: Either<String, Int> = Either.Right(3)

        val validation = Validation(e1, e2, e3)
        assertTrue(validation.hasFailures)
        assertEquals(validation.failures, listOf("Not a number"))
    }
}