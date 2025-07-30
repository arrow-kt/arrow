@file:OptIn(ExperimentalContracts::class)

package arrow.core

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.common.reflection.bestName
import io.kotest.matchers.assertionCounter
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.beOfType
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
    null -> throw AssertionErrorBuilder.create()
      .withMessage("Expected exception ${expectedExceptionClass.bestName()} but no exception was thrown.")
      .build()
    is T -> thrownThrowable
    is AssertionError -> throw thrownThrowable
    else -> throw AssertionErrorBuilder.create()
      .withMessage("Expected exception ${expectedExceptionClass.bestName()} but a ${thrownThrowable::class.simpleName} was thrown instead.")
      .withCause(thrownThrowable)
      .build()
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

  return thrownException ?: fail("Expected a throwable, but nothing was thrown.")
}
