@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
package arrow.core.raise

import arrow.atomic.Atomic
import arrow.atomic.updateAndGet
import arrow.core.*
import arrow.typeclasses.Semigroup
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <E, A> either(@BuilderInference block: Raise<E>.() -> A): Either<E, A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block.invoke(this) }, { Either.Left(it) }, { Either.Right(it) })
}

public inline fun <A> nullable(block: NullableRaise.() -> A): A? {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block(NullableRaise(this)) }, { null }, ::identity)
}

public inline fun <A> result(block: ResultRaise.() -> A): Result<A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block(ResultRaise(this)) }, Result.Companion::failure, Result.Companion::success)
}

public inline fun <reified A> option(block: OptionRaise.() -> A): Option<A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return fold({ block(OptionRaise(this)) }, ::identity, ::Some)
}

public inline fun <E, A> ior(semigroup: Semigroup<E>, @BuilderInference block: IorRaise<E>.() -> A): Ior<E, A> {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  val state: Atomic<Option<E>> = Atomic(None)
  return fold<E, A, Ior<E, A>>(
    { block(IorRaise(semigroup, state, this)) },
    { e -> throw e },
    { e -> Ior.Left(state.get().getOrElse<Any?> { e } as E) },
    { a -> state.get().fold<Any?, Ior<E, A>>({ Ior.Right(a) }, { Ior.Both(it as E, a) }) }
  )
}

public typealias Null = Nothing?

@JvmInline
public value class NullableRaise(private val cont: Raise<Null>) : Raise<Null> {
  @RaiseDSL
  public fun ensure(value: Boolean): Unit = ensure(value) { null }
  override fun raise(r: Nothing?): Nothing = cont.raise(r)
  public inline fun <reified B> Option<B>.bind(): B = bind { raise(null) }

  public fun <B> B?.bind(): B {
    contract { returns() implies (this@bind != null) }
    return this ?: raise(null)
  }

  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { null }
  }
}

@JvmInline
public value class ResultRaise(private val cont: Raise<Throwable>) : Raise<Throwable> {
  override fun raise(r: Throwable): Nothing = cont.raise(r)
  public fun <B> Result<B>.bind(): B = fold(::identity) { raise(it) }
}

@JvmInline
public value class OptionRaise(private val cont: Raise<Option<Nothing>>) : Raise<Option<Nothing>> {
  override fun raise(r: Option<Nothing>): Nothing = cont.raise(r)
  public inline fun <reified B> Option<B>.bind(): B = bind { raise(None) }
  public fun ensure(value: Boolean): Unit = ensure(value) { None }

  public fun <B> ensureNotNull(value: B?): B {
    contract { returns() implies (value != null) }
    return ensureNotNull(value) { None }
  }
}

public class IorRaise<E> @PublishedApi internal constructor(
  semigroup: Semigroup<E>,
  private val state: Atomic<Option<E>>,
  private val raise: Raise<E>,
) : Raise<E>, Semigroup<E> by semigroup {

  override fun raise(r: E): Nothing = raise.raise(combine(r))

  public fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> raise(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  // We're using Some<Any?> here so that if E happens to be an Option, we store it as is
  // i.e. with no denesting. This is so that when we map over it for instance, we have no idea
  // whether E is an option or not, so we use Some<Any?> instead of risking a CCE.
  // We also must use Any? as the type parameter for map and getOrElse because we don't have the
  // reified E type. We have to be careful though, since if someone else had access to our $state
  // and called getOrElse on it with a reified E, and E happened to be an Option<X>, they'd end up getting
  // an Option<Option<X>>, where unpacking that option would result in a CCE when the value is used as an X.
  private fun combine(other: E): E =
    state.updateAndGet { prev ->
      prev.map<Any?, Any?> { e -> (e as E).combine(other) }.orElse { Some<Any?>(other) } as Option<E>
    }.getOrElse<Any?> { other } as E
}
