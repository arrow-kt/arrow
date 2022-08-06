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
public value class OptionShift(private val cont: Shift<None>) : Shift<None> {
  override suspend fun <B> shift(r: None): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { None }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { None }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> OptionShift.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { None }
}

@OptIn(ExperimentalContracts::class)
public suspend fun <B> OptionEagerShift.ensureNotNull(value: B?): B {
  contract { returns() implies (value != null) }
  return ensureNotNull(value) { None }
}

@JvmInline
public value class OptionEagerShift(private val cont: EagerShift<None>) : EagerShift<None> {
  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: None): B =
    cont.shift(r)

  public suspend fun <B> Option<B>.bind(): B =
    bind { None }

  public suspend fun ensure(value: Boolean): Unit =
    ensure(value) { None }
}

@Suppress("ClassName")
public object option {
  public inline fun <A> eager(crossinline f: suspend OptionEagerShift.() -> A): Option<A> =
    eagerEffect<None, A> {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      f(OptionEagerShift(this))
    }.toOption()

  public suspend inline operator fun <A> invoke(crossinline f: suspend OptionShift.() -> A): Option<A> =
    effect<None, A> { f(OptionShift(this)) }.toOption()
}
