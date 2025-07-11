package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equalityMatcher
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

fun <A> Arb.Companion.flow(arbA: Arb<A>, range: IntRange = 1 .. 20): Arb<Flow<A>> =
  Arb.choose(
    10 to Arb.list(arbA, range).map { it.asFlow() },
    10 to Arb.list(arbA, range).map { l -> channelFlow { l.forEach { send(it) } }.buffer(Channel.RENDEZVOUS) },
    1 to Arb.constant(emptyFlow()),
  )

fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.of(listOf(IndexOutOfBoundsException(), NoSuchElementException(), IllegalArgumentException()))

fun Arb.Companion.throwableConstructor(): Arb<() -> Throwable> =
  Arb.of(listOf({ IndexOutOfBoundsException() }, { NoSuchElementException() }, { IllegalArgumentException() }))

fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

fun <A> Arb.Companion.result(arbA: Arb<A>): Arb<Result<A>> =
  Arb.choice(arbA.map(Result.Companion::success), throwable().map(Result.Companion::failure))

suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

fun leftException(e: Throwable): Matcher<Either<Throwable, *>> =
  object : Matcher<Either<Throwable, *>> {
    override fun test(value: Either<Throwable, *>): MatcherResult =
      when (value) {
        is Either.Left -> when {
          value.value::class != e::class -> MatcherResult(
            false,
            { "Expected exception of type ${e::class} but found ${value.value::class}" },
            { "Should not be exception of type ${e::class}" }
          )

          value.value.message != e.message -> MatcherResult(
            false,
            { "Expected exception with message ${e.message} but found ${value.value.message}" },
            { "Should not be exception with message ${e.message}" }
          )

          else -> MatcherResult(
            true,
            { "Expected exception of type ${e::class} and found ${value.value::class}" },
            { "Expected exception of type ${e::class} and found ${value.value::class}" }
          )
        }

        is Either.Right -> MatcherResult(
          false,
          { "Expected Either.Left with exception of type ${e::class} and found Right with ${value.value}" },
          { "Should not be Either.Left with exception" }
        )
      }
  }

fun <A> either(e: Either<Throwable, A>): Matcher<Either<Throwable, A>> =
  object : Matcher<Either<Throwable, A>> {
    override fun test(value: Either<Throwable, A>): MatcherResult =
      when (value) {
        is Either.Left -> when {
          value.value::class != (e.swap().getOrNull() ?: Int)::class -> MatcherResult(
            false,
            { "Expected $e but found $value (class check failed)" },
            { "Should not be $e" }
          )
          /* there are problems matching the message
          value.value.message != (e.swap().orNull()?.message ?: -1) -> MatcherResult(
            false,
            "Expected $e but found $value (message check failed)",
            "Should not be $e"
          ) */
          else -> MatcherResult(
            true,
            { "Expected exception of type ${e::class} and found ${value.value::class}" },
            { "Expected exception of type ${e::class} and found ${value.value::class}" }
          )
        }

        is Either.Right -> equalityMatcher(e).test(value)
      }
  }

suspend fun <A> awaitExitCase(send: Channel<Unit>, exit: CompletableDeferred<ExitCase>): A =
  guaranteeCase({
    send.receive()
    awaitCancellation()
  }) { ex -> exit.complete(ex) }

suspend fun <A> awaitExitCase(start: CompletableDeferred<Unit>, exit: CompletableDeferred<ExitCase>): A =
  guaranteeCase({
    start.complete(Unit)
    awaitCancellation()
  }) { ex -> exit.complete(ex) }

inline fun <A> assertThrowable(executable: () -> A): Throwable {
  val a = try {
    executable.invoke()
  } catch (e: Throwable) {
    e
  }

  return a as? Throwable ?: fail("Expected an exception but found: $a")
}

suspend fun <T> CompletableDeferred<T>.shouldHaveCompleted(): T {
  isCompleted shouldBe true
  return await()
}
