package arrow.integrations.jackson.module

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.matchers.assertionCounter

inline fun <T> shouldNotThrowAny(block: () -> T): T {
  assertionCounter.inc()

  val thrownException = try {
    return block()
  } catch (e: Throwable) {
    e
  }

  throw AssertionErrorBuilder.create()
    .withMessage("No exception expected, but a ${thrownException::class.simpleName} was thrown with message: \"${thrownException.message}\".")
    .withCause(thrownException)
    .build()
}
