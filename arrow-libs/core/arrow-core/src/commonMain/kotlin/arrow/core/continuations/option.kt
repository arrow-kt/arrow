package arrow.core.continuations

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

public suspend fun <A> Effect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

public fun <A> EagerEffect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

@JvmInline
public value class OptionEffectScope(private val cont: EffectScope<None>) : EffectScope<None> {
  override suspend fun <B> shift(r: None): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { None }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { None }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> OptionEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { None }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> OptionEagerEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { None }
}

@JvmInline
public value class OptionEagerEffectScope(private val cont: EagerEffectScope<None>) : EagerEffectScope<None> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: None): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { None }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { None }
}

@Deprecated(
  optionDSLDeprecation,
  ReplaceWith("option", "arrow.core.raise.option")
)
@Suppress("ClassName")
public object option {
  @Deprecated(
    optionDSLDeprecation,
    ReplaceWith("option(f)", "arrow.core.raise.option")
  )
  public inline fun <A> eager(crossinline f: suspend OptionEagerEffectScope.() -> A): Option<A> =
    eagerEffect<None, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(OptionEagerEffectScope(this))
    }.toOption()
  
  @Deprecated(
    optionDSLDeprecation,
    ReplaceWith("option(f)", "arrow.core.raise.option")
  )
  public suspend inline operator fun <A> invoke(crossinline f: suspend OptionEffectScope.() -> A): Option<A> =
    effect<None, A> { f(OptionEffectScope(this)) }.toOption()
}

@PublishedApi internal const val optionDSLDeprecation: String =
  "The option DSL has been moved to arrow.core.raise.option.\n" +
    "Replace import arrow.core.computations.option with arrow.core.raise.option"
