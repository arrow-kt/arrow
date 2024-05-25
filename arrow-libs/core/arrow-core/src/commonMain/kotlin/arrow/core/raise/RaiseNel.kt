@file:OptIn(ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.reflect.KProperty

public fun <Error, A> Raise<NonEmptyList<Error>>.accumulate(
  block: RaiseNel<Error>.() -> A
): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  val nel = RaiseNel(this)
  val result = block(nel)
  if (nel.hasErrors()) nel.raiseErrors()
  return result
}

public fun <Error, A, R> accumulate(
  raise: (Raise<NonEmptyList<Error>>.() -> A) -> R,
  block: RaiseNel<Error>.() -> A
): R {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return raise { accumulate(block) }
}

public class RaiseNel<Error>(
  private val raise: Raise<NonEmptyList<Error>>
): Raise<Error> {
  private val errors: MutableList<Error> = mutableListOf()

  override fun raise(r: Error): Nothing = raise.raise((errors + r).toNonEmptyListOrNull()!!)
  internal fun hasErrors(): Boolean = errors.isNotEmpty()
  internal fun raiseErrors(): Nothing = raise.raise(errors.toNonEmptyListOrNull()!!)

  public fun <A> Either<Error, A>.bindAccumulating(): Value<A> =
    accumulating { this@bindAccumulating.bind() }

  public fun <A> Iterable<Either<Error, A>>.bindAllAccumulating(): Value<List<A>> =
    accumulating { this@bindAllAccumulating.bindAll() }

  public fun <A> EitherNel<Error, A>.bindNelAccumulating(): Value<A> =
    accumulating { this@bindNelAccumulating.bindNel() }

  public fun ensure(condition: Boolean, raise: () -> Error) {
    accumulating { ensure(condition, raise) }
  }

  public fun <B: Any> ensureNotNull(value: B?, raise: () -> Error) {
    ensure(value != null, raise)
  }

  public fun <A> accumulating(block: RaiseAccumulate<Error>.() -> A): Value<A> =
    recover({
      Ok(block(RaiseAccumulate(this)))
    }) {
      errors.addAll(it)
      Error()
    }

  public abstract inner class Value<out A> {
    public abstract val result: A
    public operator fun getValue(value: Nothing?, property: KProperty<*>): A = result
  }
  internal inner class Error: Value<Nothing>() {
    override val result: Nothing = raiseErrors()
  }
  internal inner class Ok<out A>(override val result: A): Value<A>()
}
