@file:OptIn(ExperimentalContracts::class)
package arrow.raise.ktor.server

import arrow.core.*
import arrow.core.raise.*
import arrow.core.raise.mapValuesOrAccumulate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.reflect.KProperty

public inline fun <Error, A> Raise<NonEmptyList<Error>>.accumulate(
  block: RaiseAccumulate<Error>.() -> A
): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  val nel = RaiseAccumulate(this)
  val result = block(nel)
  if (nel.hasErrors()) nel.raiseErrors()
  return result
}

public open class RaiseAccumulate<Error>(
  public val raise: Raise<NonEmptyList<Error>>
) : Raise<Error> {

  internal val errors: MutableList<Error> = mutableListOf()

  @RaiseDSL
  public override fun raise(r: Error): Nothing =
    raise.raise((errors + r).toNonEmptyListOrNull()!!)

  public override fun <K, A> Map<K, Either<Error, A>>.bindAll(): Map<K, A> =
    raise.mapValuesOrAccumulate(this) { it -> it.value.bind() }

  @RaiseDSL
  public fun <A> EitherNel<Error, A>.bindNel(): A = when (this) {
    is Either.Left -> raise.raise(value)
    is Either.Right -> value
  }

  @RaiseDSL
  public inline fun <A> withNel(block: Raise<NonEmptyList<Error>>.() -> A): A {
    contract {
      callsInPlace(block, EXACTLY_ONCE)
    }
    return block(raise)
  }

  @PublishedApi internal fun addErrors(newErrors: Iterable<Error>) { errors.addAll(newErrors) }
  @PublishedApi internal fun hasErrors(): Boolean = errors.isNotEmpty()
  @PublishedApi internal fun raiseErrors(): Nothing = raise.raise(errors.toNonEmptyListOrNull()!!)

  public fun <A> Either<Error, A>.bindOrAccumulate(): Value<A> =
    accumulating { this@bindOrAccumulate.bind() }

  public fun <A> Iterable<Either<Error, A>>.bindAllOrAccumulate(): Value<List<A>> =
    accumulating { this@bindAllOrAccumulate.bindAll() }

  public fun <A> EitherNel<Error, A>.bindNelOrAccumulate(): Value<A> =
    accumulating { this@bindNelOrAccumulate.bindNel() }

  public fun ensureOrAccumulate(condition: Boolean, raise: () -> Error) {
    accumulating { ensure(condition, raise) }
  }

  public fun <B: Any> ensureNotNullOrAccumulate(value: B?, raise: () -> Error) {
    ensureOrAccumulate(value != null, raise)
  }

  public inline fun <A> accumulating(block: RaiseAccumulate<Error>.() -> A): Value<A> =
    recover(inner@{
      Ok(block(RaiseAccumulate(this@inner)))
    }) {
      addErrors(it)
      Error()
    }

  public abstract inner class Value<out A> {
    public abstract val result: A
    public operator fun getValue(value: Nothing?, property: KProperty<*>): A = result
  }
  @PublishedApi internal inner class Error: Value<Nothing>() {
    override val result: Nothing
      // WARNING: do not turn this into a value with initializer!!
      //          'raiseErrors' is then executed eagerly, and leads to wrong behavior!!
      get() {
        raiseErrors()
      }
  }
  @PublishedApi internal inner class Ok<out A>(override val result: A): Value<A>()
}
