package arrow.core.continuations

import arrow.core.Option
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

@Deprecated(
  "NullableEffectScope is replaced with arrow.core.raise.NullableRaise",
  ReplaceWith("NullableRaise", "arrow.core.raise.NullableRaise")
)
@JvmInline
public value class NullableEffectScope(private val cont: EffectScope<Nothing?>) : EffectScope<Nothing?> {
  override suspend fun <B> shift(r: Nothing?): B =
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

@Deprecated(
  "NullableEagerEffectScope is replaced with arrow.core.raise.NullableRaise",
  ReplaceWith("NullableRaise", "arrow.core.raise.NullableRaise")
)
@JvmInline
public value class NullableEagerEffectScope(private val cont: EagerEffectScope<Nothing?>) : EagerEffectScope<Nothing?> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: Nothing?): B =
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
public suspend fun <B> NullableEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> NullableEagerEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { null }
}

@Deprecated(nullableDSLDeprecation, ReplaceWith("nullable", "arrow.core.raise.nullable"))
@Suppress("ClassName")
public object nullable {
  @Deprecated(nullableDSLDeprecation, ReplaceWith("nullable(f)", "arrow.core.raise.nullable"))
  public inline fun <A> eager(crossinline f: suspend NullableEagerEffectScope.() -> A): A? =
    eagerEffect<Nothing?, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(NullableEagerEffectScope(this))
    }.orNull()
  
  @Deprecated(nullableDSLDeprecation, ReplaceWith("nullable(f)", "arrow.core.raise.nullable"))
  public suspend inline operator fun <A> invoke(crossinline f: suspend NullableEffectScope.() -> A): A? =
    effect<Nothing?, A> { f(NullableEffectScope(this)) }.orNull()
}

private const val nullableDSLDeprecation =
  "The nullable DSL has been moved to arrow.core.raise.nullable.\n" +
    "Replace import arrow.core.computations.nullable with arrow.core.raise.nullable"
