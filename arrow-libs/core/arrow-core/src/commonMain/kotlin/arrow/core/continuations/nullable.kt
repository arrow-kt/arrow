package arrow.core.continuations

import arrow.core.Option
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

@JvmInline
public value class NullableEffectScope<R>(private val cont: EffectScope<R?>) : EffectScope<R?> {
  override suspend fun <B> shift(r: R?): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { null }

  @OptIn(ExperimentalContracts::class)
  public suspend fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: shift(null)
  }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { null }
}

@JvmInline
public value class NullableEagerEffectScope<R>(private val cont: EagerEffectScope<R?>) : EagerEffectScope<R?> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: R?): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { null }

  @OptIn(ExperimentalContracts::class)
  public suspend fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: shift(null)
  }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { null }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <R : Any, B> NullableEffectScope<R?>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <R : Any, B> NullableEagerEffectScope<R?>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@Suppress("ClassName")
public object nullable {
  public inline fun <A> eager(crossinline f: suspend NullableEagerEffectScope<Any?>.() -> A): A? =
    eagerEffect<Any?, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(NullableEagerEffectScope(this))
    }.orNull()

  public suspend inline operator fun <A> invoke(crossinline f: suspend NullableEffectScope<Any?>.() -> A): A? =
    effect<Any?, A> { f(NullableEffectScope(this)) }.orNull()
}
