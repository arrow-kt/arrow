package arrow.core.continuations

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

public suspend inline fun <A> option(crossinline f: suspend OptionEffectScope.() -> A): Option<A> =
  effect<None, A> { f(OptionEffectScope(this)) }.toOption()

public suspend fun <A> Effect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

@JvmInline
public value class OptionEffectScope(private val cont: EffectScope<None>) : EffectScope<None> {
  public suspend fun <B> Option<B>.bind(): B = bind { None }

  public suspend fun ensure(value: Boolean): Unit = if (value) Unit else shift(None)

  override suspend fun <B> shift(r: None): B = cont.shift(r)
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B : Any> EffectScope<None>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return value ?: shift(None)
}
