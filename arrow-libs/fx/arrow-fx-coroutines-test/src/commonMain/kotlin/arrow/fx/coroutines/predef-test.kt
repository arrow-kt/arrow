package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.Validated
import arrow.core.ValidatedNel
import arrow.core.identity
import arrow.core.invalid
import arrow.core.invalidNel
import arrow.core.left
import arrow.core.right
import arrow.core.valid
import arrow.core.validNel
import io.kotest.assertions.fail
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equalityMatcher
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine

public data class SideEffect(var counter: Int = 0) {
  public fun increment() {
    counter++
  }
}

public fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.string().map(::RuntimeException)

public fun <L, R> Arb.Companion.either(left: Arb<L>, right: Arb<R>): Arb<Either<L, R>> {
  val failure: Arb<Either<L, R>> = left.map { l -> l.left() }
  val success: Arb<Either<L, R>> = right.map { r -> r.right() }
  return Arb.choice(failure, success)
}

public fun <L, R> Arb.Companion.validated(left: Arb<L>, right: Arb<R>): Arb<Validated<L, R>> {
  val failure: Arb<Validated<L, R>> = left.map { l -> l.invalid() }
  val success: Arb<Validated<L, R>> = right.map { r -> r.valid() }
  return Arb.choice(failure, success)
}

public fun <L, R> Arb.Companion.validatedNel(left: Arb<L>, right: Arb<R>): Arb<ValidatedNel<L, R>> {
  val failure: Arb<ValidatedNel<L, R>> = left.map { l -> l.invalidNel() }
  val success: Arb<ValidatedNel<L, R>> = right.map { r -> r.validNel() }
  return Arb.choice(failure, success)
}

public fun Arb.Companion.intRange(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Arb<IntRange> =
  Arb.bind(Arb.int(min, max), Arb.int(min, max)) { a, b ->
    if (a < b) a..b else b..a
  }

public fun Arb.Companion.longRange(min: Long = Long.MIN_VALUE, max: Long = Long.MAX_VALUE): Arb<LongRange> =
  Arb.bind(Arb.long(min, max), Arb.long(min, max)) { a, b ->
    if (a < b) a..b else b..a
  }

public fun Arb.Companion.charRange(): Arb<CharRange> =
  Arb.bind(Arb.char(), Arb.char()) { a, b ->
    if (a < b) a..b else b..a
  }

public fun <O> Arb.Companion.function(arb: Arb<O>): Arb<() -> O> =
  arb.map { { it } }

public fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

public fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

public fun <A> Arb.Companion.nullable(arb: Arb<A>): Arb<A?> =
  Arb.Companion.choice(arb, arb.map { null })

/** Useful for testing success & error scenarios with an `Either` generator **/
public fun <A> Either<Throwable, A>.rethrow(): A =
  fold({ throw it }, ::identity)

public fun <A> Result<A>.toEither(): Either<Throwable, A> =
  fold({ a -> Either.Right(a) }, { e -> Either.Left(e) })

public suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

public suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

public fun <A> A.suspended(): suspend () -> A =
  suspend { suspend() }

/**
 * Example usage:
 * ```kotlin
 * val exception = assertThrows<IllegalArgumentException> {
 *     throw IllegalArgumentException("Talk to a duck")
 * }
 * assertEquals("Talk to a duck", exception.message)
 * ```
 * @see Assertions.assertThrows
 */
public inline fun <A> assertThrowable(executable: () -> A): Throwable {
  val a = try {
    executable.invoke()
  } catch (e: Throwable) {
    e
  }

  return if (a is Throwable) a else fail("Expected an exception but found: $a")
}

public suspend fun CoroutineContext.shift(): Unit =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(this) {
        cont.resume(Unit)
      }
    )

    COROUTINE_SUSPENDED
  }

public fun leftException(e: Throwable): Matcher<Either<Throwable, *>> =
  object : Matcher<Either<Throwable, *>> {
    override fun test(value: Either<Throwable, *>): MatcherResult =
      when (value) {
        is Either.Left -> when {
          value.value::class != e::class -> MatcherResult(
            false,
            "Expected exception of type ${e::class} but found ${value.value::class}",
            "Should not be exception of type ${e::class}"
          )
          value.value.message != e.message -> MatcherResult(
            false,
            "Expected exception with message ${e.message} but found ${value.value.message}",
            "Should not be exception with message ${e.message}"
          )
          else -> MatcherResult(
            true,
            "Expected exception of type ${e::class} and found ${value.value::class}",
            "Expected exception of type ${e::class} and found ${value.value::class}"
          )
        }
        is Either.Right -> MatcherResult(
          false,
          "Expected Either.Left with exception of type ${e::class} and found Right with ${value.value}",
          "Should not be Either.Left with exception"
        )
      }
  }

public fun <A> either(e: Either<Throwable, A>): Matcher<Either<Throwable, A>> =
  object : Matcher<Either<Throwable, A>> {
    override fun test(value: Either<Throwable, A>): MatcherResult =
      when (value) {
        is Either.Left -> when {
          value.value::class != (e.swap().orNull() ?: Int)::class -> MatcherResult(
            false,
            "Expected $e but found $value",
            "Should not be $e"
          )
          value.value.message != (e.swap().orNull()?.message ?: -1) -> MatcherResult(
            false,
            "Expected $e but found $value",
            "Should not be $e"
          )
          else -> MatcherResult(
            true,
            "Expected exception of type ${e::class} and found ${value.value::class}",
            "Expected exception of type ${e::class} and found ${value.value::class}"
          )
        }
        is Either.Right -> equalityMatcher(e).test(value)
      }
  }
