package arrow.core.continuations

import arrow.core.Maybe
import arrow.core.Option
import arrow.core.bind
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

@JvmInline
public value class NullableEffectScope(private val cont: EffectScope<Nothing?>) : EffectScope<Nothing?> {
  override suspend fun <B> shift(r: Nothing?): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { null }
  public suspend inline fun <reified B : Any> Maybe<B>.bind(): B =
    bind(this) { null }
  public suspend fun <B : Any> Maybe<Maybe<B>>.bind(): Maybe<B> =
    bind(this) { null }

  @OptIn(ExperimentalContracts::class)
  public suspend fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: shift(null)
  }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { null }
}

@JvmInline
public value class NullableEagerEffectScope(private val cont: EagerEffectScope<Nothing?>) : EagerEffectScope<Nothing?> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: Nothing?): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { null }

  public suspend inline fun <reified B : Any> Maybe<B>.bind(): B =
    bind(this) { null }
  public suspend fun <B : Any> Maybe<Maybe<B>>.bind(): Maybe<B> =
    bind(this) { null }

  @OptIn(ExperimentalContracts::class)
  public suspend fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: shift(null)
  }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { null }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> NullableEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> NullableEagerEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@Suppress("ClassName")
public object nullable {
  public inline fun <A> eager(crossinline f: suspend NullableEagerEffectScope.() -> A): A? =
    eagerEffect<Nothing?, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(NullableEagerEffectScope(this))
    }.orNull()

  public suspend inline operator fun <A> invoke(crossinline f: suspend NullableEffectScope.() -> A): A? =
    effect<Nothing?, A> { f(NullableEffectScope(this)) }.orNull()
}
