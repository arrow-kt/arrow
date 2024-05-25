@file:OptIn(ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.toNonEmptyListOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.reflect.KProperty

public fun <Error, A> Raise<NonEmptyList<Error>>.nel(
  block: RaiseNel<Error>.() -> A
): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  val nel = RaiseNel(this)
  val result = block(nel)
  if (nel.hasErrors()) nel.raiseErrors()
  return result
}

public fun <Error, A> eitherNel(block: RaiseNel<Error>.() -> A): Either<NonEmptyList<Error>, A> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return fold({ nel(block) }, { Either.Left(it) }, { Either.Right(it) })
}

public class RaiseNel<Error>(
  private val raise: Raise<NonEmptyList<Error>>
): Raise<Error> {
  private val errors: MutableList<Error> = mutableListOf()

  override fun raise(r: Error): Nothing = raise.raise((errors + r).toNonEmptyListOrNull()!!)
  internal fun hasErrors(): Boolean = errors.isNotEmpty()
  internal fun raiseErrors(): Nothing = raise.raise(errors.toNonEmptyListOrNull()!!)

  public fun <A> accumulating(block: RaiseAccumulate<Error>.() -> A): Value<A> =
    recover({
      Ok(block(RaiseAccumulate(this)))
    }) {
      errors.addAll(it)
      Error()
    }

  public abstract inner class Value<out A> {
    public abstract operator fun getValue(value: Nothing?, property: KProperty<*>): A
  }
  internal inner class Error: Value<Nothing>() {
    override fun getValue(value: Nothing?, property: KProperty<*>): Nothing = raiseErrors()
  }
  internal inner class Ok<out A>(val result: A): Value<A>() {
    override fun getValue(value: Nothing?, property: KProperty<*>): A = result
  }
}
