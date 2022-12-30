package arrow.core.continuations

import arrow.core.EmptyValue
import arrow.core.NonEmptyList
import arrow.core.emptyCombine
import arrow.core.identity
import arrow.core.nel
import arrow.typeclasses.Semigroup

@DslMarker
public annotation class AccumulateDSL

/**
 * The [Accumulate] DSL allows to carve out a scope in which
 * errors coming from different validations are _accumulated_,
 * instead of bailing out on the first error. Each call which
 * may [Raise] a potential problem must be _protected_ using
 * [accumulate].
 *
 * The accumulation of errors stops at the first moment in which
 * there's a data dependency on a previous computation which
 * returned with error. To make those dependencies explicit,
 * the result of [accumulate] is a [Value], which you need to
 * unwrap.
 *
 * ```kotlin
 * data class Name(val first: String, val last: String)
 *
 * fun Raise<String>.validFirstName(s: String): String = TODO()
 * fun Raise<String>.validLastName(s: String): String = TODO()
 *
 * fun Raise<NonEmptyList<String>>.validName(
 *   first: String, last: String
 * ): Name = accumulateErrors {
 *   val f = accumulate { validFirstname(first) }
 *   val l = accumulate { validLastName(last) }
 *   Name(f.value, l.value)
 * }
 * ```
 *
 * You need to be careful to ensure that you only access `value`
 * after all the necessary calls to [accumulate].
 */
public interface Accumulate<R> {
  /**
   * Represents a potential value of type [A]. This is used in
   * combination with [Accumulate] to track data dependencies.
   */
  public interface Value<out A> {
    public val value: A
    public val isSuccess: Boolean
    public val isFailure: Boolean
      get() = !isSuccess
  }
  @AccumulateDSL
  public fun <B> accumulate(action: Raise<R>.() -> B): Value<B>
}

/**
 * Accumulates the errors in [block] as a [NonEmptyList].
 *
 * See the documentation for [Accumulate] for a description of the
 * methods available inside the block.
 */
@EffectDSL
public fun <R, A> Raise<NonEmptyList<R>>.accumulateErrors(
  block: Accumulate<R>.() -> A
): A = accumulateErrors(Semigroup.nonEmptyList(), { it.nel() }, block)

/**
 * Accumulates the errors in [block] using the given [semigroup].
 *
 * See the documentation for [Accumulate] for a description of the
 * methods available inside the block.
 */
@EffectDSL
public fun <R, A> Raise<R>.accumulateErrors(
  semigroup: Semigroup<R>,
  block: Accumulate<R>.() -> A
): A = accumulateErrors(semigroup, ::identity, block)

/**
 * Used to track nested [accumulateErrors].
 */
private class AccumulatorToken()

/**
 * This exception is thrown by [Accumulate.Value] to indicate
 * that accessing a missing value was attempted, and thus the
 * accumulation of errors must finish.
 */
private class DataDependencyException(val token: AccumulatorToken): Exception()

private fun <E, R, A> Raise<E>.accumulateErrors(
  semigroup: Semigroup<E>,
  inject: (R) -> E,
  block: Accumulate<R>.() -> A
): A {
  val token = AccumulatorToken() // create a new token to distinguish nested accumulate
  val accumulator = AccumulateImpl(semigroup, inject, token)
  return try {
    val result = block(accumulator)
    when (val e = accumulator.error) {
      is EmptyValue -> result
      else -> raise(EmptyValue.unbox<E>(e))
    }
  } catch (e: DataDependencyException) {
    if (e.token == token)
      raise(EmptyValue.unbox<E>(accumulator.error))
    else
      throw e
  }
}

private class AccumulateImpl<E, R>(
  val semigroup: Semigroup<E>,
  val inject: (R) -> E,
  val token: AccumulatorToken
): Accumulate<R> {
  data class OkValue<out A>(override val value: A): Accumulate.Value<A> {
    override val isSuccess: Boolean = true
  }
  data class NoValue(val token: AccumulatorToken): Accumulate.Value<Nothing> {
    override val isSuccess: Boolean = false
    override val value: Nothing
      get() = throw DataDependencyException(token)
  }

  var error: Any? = EmptyValue

  override fun <B> accumulate(action: Raise<R>.() -> B): Accumulate.Value<B> =
    fold(
      action,
      { newError ->
        error = semigroup.emptyCombine(error, inject(newError))
        NoValue(token)
      },
      { OkValue(it) }
    )
}
