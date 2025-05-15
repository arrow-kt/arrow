@file:OptIn(ExperimentalContracts::class)

package arrow.core

import io.kotest.assertions.assertionCounter
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.beOfType
import io.kotest.mpp.bestName
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

inline fun <reified T : Any> Any?.shouldBeTypeOf(): T {
  contract {
    returns() implies (this@shouldBeTypeOf is T)
  }
  val matcher = beOfType(T::class)
  this shouldBe matcher
  return this as T
}

inline fun <reified T : Any> Any?.shouldBeInstanceOf(): T {
  contract {
    returns() implies (this@shouldBeInstanceOf is T)
  }
  val matcher = beInstanceOf(T::class)
  this shouldBe matcher
  return this as T
}

inline fun <reified T : Throwable> shouldThrow(block: () -> Any?): T {
  assertionCounter.inc()
  val expectedExceptionClass = T::class
  val thrownThrowable = try {
    block()
    null  // Can't throw failure here directly, as it would be caught by the catch clause, and it's an AssertionError, which is a special case
  } catch (thrown: Throwable) {
    thrown
  }

  return when (thrownThrowable) {
    null -> throw failure("Expected exception ${expectedExceptionClass.bestName()} but no exception was thrown.")
    is T -> thrownThrowable               // This should be before `is AssertionError`. If the user is purposefully trying to verify `shouldThrow<AssertionError>{}` this will take priority
    is AssertionError -> throw thrownThrowable
    else -> throw failure(
      "Expected exception ${expectedExceptionClass.bestName()} but a ${thrownThrowable::class.simpleName} was thrown instead.",
      thrownThrowable
    )
  }
}

inline fun shouldThrowAny(block: () -> Any?): Throwable {
  assertionCounter.inc()
  val thrownException = try {
    block()
    null
  } catch (e: Throwable) {
    e
  }

  return thrownException ?: throw failure("Expected a throwable, but nothing was thrown.")
}
