package arrow.core.continuations

import arrow.core.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

public suspend fun <A: Any> Effect<Maybe<Nothing>, A>.toMaybe(): Maybe<A> =
  fold(::identity) { Just(it) }

public fun <A: Any> EagerEffect<Maybe<Nothing>, A>.toMaybe(): Maybe<A> =
  fold(::identity) { Just(it) }

@JvmInline
public value class MaybeEffectScope(private val cont: EffectScope<Maybe<Nothing>>) : EffectScope<Maybe<Nothing>> {
  override suspend fun <B> shift(r: Maybe<Nothing>): B =
    cont.shift(r)

  public suspend inline fun <reified B: Any> Maybe<B>.bind(): B =
    bind(this) { Maybe.Nothing }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { Maybe.Nothing }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> MaybeEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { Maybe.Nothing }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> MaybeEagerEffectScope.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { Maybe.Nothing }
}

@JvmInline
public value class MaybeEagerEffectScope(private val cont: EagerEffectScope<Maybe<Nothing>>) : EagerEffectScope<Maybe<Nothing>> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: Maybe<Nothing>): B =
    cont.shift(r)

  public suspend inline fun <reified B: Any> Maybe<B>.bind(): B =
    bind(this) { Maybe.Nothing }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { Maybe.Nothing }
}

@Suppress("ClassName")
public object maybe {
  public inline fun <A: Any> eager(crossinline f: suspend MaybeEagerEffectScope.() -> A): Maybe<A> =
    eagerEffect<Maybe<Nothing>, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(MaybeEagerEffectScope(this))
    }.toMaybe()

  public suspend inline operator fun <A: Any> invoke(crossinline f: suspend MaybeEffectScope.() -> A): Maybe<A> =
    effect<Maybe<Nothing>, A> { f(MaybeEffectScope(this)) }.toMaybe()
}
