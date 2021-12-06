@file:JvmMultifileClass
@file:JvmName("ControlKt")
package arrow.core.coroutines

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

public suspend fun <A : Any> nullable(f: NullableEffect<A?>.() -> A): A? =
  control<A?, A> { f(NullableEffect(this)) }.orNull()

public suspend fun <A> Control<A?, A>.orNull(): A? = fold(::identity, ::identity)

public class NullableEffect<A>(private val cont: ControlEffect<A?>) : ControlEffect<A?> {
  public suspend fun <B : Any> B?.bind(): B = this ?: shift(null)

  public suspend fun ensure(value: Boolean): Unit = if (value) Unit else shift(null)

  override suspend fun <B> shift(r: A?): B = cont.shift(r)
}

@OptIn(
  ExperimentalContracts::class
) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> ControlEffect<Any?>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return value ?: shift(null)
}
