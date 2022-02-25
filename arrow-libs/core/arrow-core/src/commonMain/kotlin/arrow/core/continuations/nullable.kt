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
public suspend fun <R, B> NullableEffectScope<R?>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <R, B> NullableEagerEffectScope<R?>.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@Suppress("ClassName")
public object nullable {
  public inline fun <R, A> eager(crossinline f: suspend NullableEagerEffectScope<R?>.() -> A): A? =
    eagerEffect<R?, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(NullableEagerEffectScope(this))
    }.orNull()

  public suspend inline operator fun <R, A> invoke(crossinline f: suspend NullableEffectScope<R?>.() -> A): A? =
    effect<R?, A> { f(NullableEffectScope(this)) }.orNull()
}
