package arrow.core.raise

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("UNREACHABLE_CODE")
class ResultSpec : StringSpec({
  val boom = RuntimeException("Boom!")

  "Result - exception" {
    result<Nothing> {
      throw boom
    } shouldBe Result.failure(boom)
  }

  "Result - success" {
    result { 1 } shouldBe Result.success(1)
  }

  "Result - raise" {
    result<Nothing> { raise(boom) } shouldBe Result.failure(boom)
  }

  "Recover works as expected" {
    result {
      val one: Int = recover({ Result.failure<Int>(boom).bind() }) { 1 }
      val two = Result.success(2).bind()
      one + two
    } shouldBe Result.success(3)
  }

  "Detects potential leaked exceptions" {
    @Suppress("DEPRECATION_ERROR")
    shouldThrow<IllegalStateException> {
      result { lazy { raise(Exception()) } }
    }
  }

  "Unsafe leakage of exceptions" {
    val l: Lazy<Int> = resultUnsafe { lazy { raise(Exception()) } }.getOrThrow()
    shouldThrow<IllegalStateException> {
      l.value
    }
  }
})
