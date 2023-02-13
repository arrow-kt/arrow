package generic

import arrow.continuations.Reset
import arrow.continuations.generic.RestrictedScope
import arrow.core.Either
import arrow.core.Either.Left
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SingleShotContTestSuite : StringSpec({
    "yield a list (also verifies stacksafety)" {
      Reset.restricted {
        suspend fun <A> RestrictedScope<List<A>>.yield(a: A): Unit = shift { k -> listOf(a) + k(Unit) }
        for (i in 0..10_000) yield(i)
        emptyList()
      } shouldBe (0..10_000).toList()
    }
    "short circuit" {
      Reset.restricted<Either<String, Int>> {
        val no: Int = shift { Left("No thank you") }
        throw IllegalStateException("This should not be executed")
      } shouldBe Left("No thank you")
    }
})
