@file:JvmMultifileClass
@file:JvmName("ControlKt")
package arrow.core.coroutines

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

public suspend fun <A> option(f: OptionEffect.() -> A): Option<A> =
  control<None, A> { f(OptionEffect(this)) }.toOption()

public suspend fun <A> Control<None, A>.toOption(): Option<A> = fold(::identity) { Some(it) }

@JvmInline
public value class OptionEffect(private val cont: ControlEffect<None>) : ControlEffect<None> {
  public suspend fun <B> Option<B>.bind(): B = bind { None }

  public suspend fun ensure(value: Boolean): Unit = if (value) Unit else shift(None)

  override suspend fun <B> shift(r: None): B = cont.shift(r)
}

@OptIn(
  ExperimentalContracts::class
) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> ControlEffect<None>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return value ?: shift(None)
}
