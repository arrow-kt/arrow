package arrow.fx.resilience

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.nonFatalOrThrow
import arrow.core.some
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlin.jvm.JvmInline
import kotlin.time.Duration

public typealias Schedule2<Delay, Input, Output> = Next2<Delay, Input, Output>

@JvmInline
public value class Next2<Delay, in Input, out Output>(
  public val step: suspend (Input) -> Decision2<Delay, Input, Output>
) {
  // any structure over the inner suspended function can be lifted

  public fun <A, B, C> lift(
    f: (suspend (Input) -> Decision2<Delay, Input, Output>) -> (suspend (B) -> Decision2<A, B, C>)
  ): Next2<A, B, C> = Next2(f(step))

  public fun <T> mapDelay(f: (Delay) -> T): Next2<T, Input, Output> =
    lift { step -> { i: Input -> step(i).mapDelay(f) } }

  public fun <A> contramap(f: (A) -> Input): Next2<Delay, A, Output> =
    lift { step -> { a: A -> step(f(a)).contramap(f) } }

  public fun <A> map(f: (Output) -> A): Next2<Delay, Input, A> =
    lift { step -> { i: Input -> step(i).map(f) } }

  // this is the sequential structure

  public fun then(
    inBetweenDelay: Delay,
    continuation: Next2<Delay, @UnsafeVariance Input, @UnsafeVariance Output>
  ): Next2<Delay, Input, Output> = Next2 { i: Input -> step(i).then(inBetweenDelay, continuation.step) }

  // any parallel Applicative structure in Next2 can be lifted here

  public fun <A, B> zip(
    other: Next2<Delay, @UnsafeVariance Input, A>,
    f: (Decision2<Delay, Input, Output>, Decision2<Delay, Input, A>) -> Decision2<Delay, @UnsafeVariance Input, B>
  ): Next2<Delay, Input, B> = Next2 { i: Input -> f(step(i), other.step(i)) }

  public fun <A, B> and(
    other: Next2<Delay, @UnsafeVariance Input, A>,
    f: (Output, A) -> B,
    combineDuration: (left: Delay, right: Delay) -> Delay
  ): Next2<Delay, Input, B> = this.zip(other) { x, y -> x.and(y, f, combineDuration) }

  public fun <A, B> or(
    other: Next2<Delay, @UnsafeVariance Input, A>,
    f: (Output?, A?) -> B,
    combineDuration: (left: Delay, right: Delay) -> Delay
  ): Next2<Delay, Input, B> = this.zip(other) { x, y -> x.or(y, f, combineDuration) }
}

public sealed interface Decision2<Delay, in Input, out Output> {
  public val output: Output

  public data class Done<Delay, out Output>(override val output: Output) : Decision2<Delay, Any?, Output>
  public data class Continue<Delay, in Input, out Output>(
    override val output: Output,
    val delay: Delay,
    val next: Next2<Delay, Input, Output>
  ) : Decision2<Delay, Input, Output> {
    public companion object {
      public operator fun <Delay, Input, Output> invoke(
        output: Output,
        delay: Delay,
        next: suspend (Input) -> Decision2<Delay, Input, Output>
      ): Continue<Delay, Input, Output> = Continue(output, delay, Next2(next))
    }
  }

  public fun <T> mapDelay(f: (Delay) -> T): Decision2<T, Input, Output> = when (this) {
    is Done -> Done(output)
    is Continue -> Continue(output, f(delay), next.mapDelay(f))
  }

  public fun <A> contramap(f: (A) -> Input): Decision2<Delay, A, Output> = when (this) {
    is Done -> Done(output)
    is Continue -> Continue(output, delay, next.contramap(f))
  }

  public fun <A> map(f: (Output) -> A): Decision2<Delay, Input, A> = when (this) {
    is Done -> Done(f(output))
    is Continue -> Continue(f(output), delay, next.map(f))
  }

  // you can combine them in three different ways

  // 1. sequentially

  public fun then(
    inBetweenDelay: Delay,
    continuation: (@UnsafeVariance Input) -> Decision2<Delay, @UnsafeVariance Input, @UnsafeVariance Output>
  ): Continue<Delay, Input, Output> = when (this) {
    is Done -> Continue(output, inBetweenDelay, continuation)
    is Continue -> Continue(output, this.delay) { x -> this.next.step(x).then(inBetweenDelay, continuation) }
  }

  public suspend fun then(
    inBetweenDelay: Delay,
    continuation: suspend (@UnsafeVariance Input) -> Decision2<Delay, @UnsafeVariance Input, @UnsafeVariance Output>
  ): Continue<Delay, Input, Output> = when (this) {
    is Done -> Continue(output, inBetweenDelay, continuation)
    is Continue -> Continue(output, this.delay) { x -> this.next.step(x).then(inBetweenDelay, continuation) }
  }

  // Decision2 is Applicative in two different ways

  // 2. in parallel, one stops = both stop (think of 'race')

  public fun <A, B> and(
    other: Decision2<Delay, @UnsafeVariance Input, A>,
    f: (Output, A) -> B,
    combineDelay: (left: Delay, right: Delay) -> Delay
  ): Decision2<Delay, Input, B> = when {
    this is Continue && other is Continue ->
      Continue(
        f(this.output, other.output),
        combineDelay(this.delay, other.delay),
        this.next.and(other.next, f, combineDelay)
      )
    else -> Done(f(this.output, other.output))
  }

  // 2. in parallel, both need to stop for everything to stop (think of 'parZip')

  public fun <A, B> or(
    other: Decision2<Delay, @UnsafeVariance Input, A>,
    f: (Output?, A?) -> B,
    combineDelay: (left: Delay, right: Delay) -> Delay
  ): Decision2<Delay, Input, B> = when {
    this is Done && other is Done -> Done(f(this.output, other.output))
    this is Done && other is Continue -> other.map { x -> f(null, x) }
    this is Continue && other is Done -> this.map { x -> f(x, null) }
    this is Continue && other is Continue ->
      Continue(
        f(this.output, other.output),
        combineDelay(this.delay, other.delay),
        this.next.or(other.next, f, combineDelay)
      )
    else -> throw IllegalStateException()
  }
}

public suspend fun <A, Input, Output> Schedule2<Duration, Input, Output>.repeatOrElseEither(
  block: suspend () -> Input,
  orElse: suspend (error: Throwable, output: Output?) -> A
): Either<A, Output> {
  var current = this
  var state: Option<Output> = None

  while (true) {
    currentCoroutineContext().ensureActive()
    try {
      val a = block.invoke()
      when (val decision = current.step(a)) {
        is Decision2.Continue -> {
          if (decision.delay != Duration.ZERO) delay(decision.delay)
          state = decision.output.some()
          current = decision.next
        }

        is Decision2.Done -> return Either.Right(decision.output)
      }
    } catch (e: Throwable) {
      return Either.Left(orElse(e.nonFatalOrThrow(), state.getOrNull()))
    }
  }
}
