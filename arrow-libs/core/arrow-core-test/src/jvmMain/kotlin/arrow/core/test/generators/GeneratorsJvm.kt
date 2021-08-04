package arrow.core.test.generators

import arrow.core.Either
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of

public fun Arb.Companion.suspendFunThatThrowsFatalThrowable(): Arb<suspend () -> Either<Any, Any>> =
  fatalThrowable().map { suspend { throw it } } as Arb<suspend () -> Either<Any, Any>>

public fun Arb.Companion.fatalThrowable(): Arb<Throwable> =
  Arb.of(listOf(ThreadDeath(), StackOverflowError(), OutOfMemoryError(), InterruptedException()))
