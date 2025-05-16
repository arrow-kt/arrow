package arrow.integrations.jackson.module

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure

inline fun <T> shouldNotThrowAny(block: () -> T): T {
  assertionCounter.inc()

  val thrownException = try {
    return block()
  } catch (e: Throwable) {
    e
  }

  throw failure(
    "No exception expected, but a ${thrownException::class.simpleName} was thrown with message: \"${thrownException.message}\".",
    thrownException
  )
}
