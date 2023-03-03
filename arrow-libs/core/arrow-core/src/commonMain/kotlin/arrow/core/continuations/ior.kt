package arrow.core.continuations

import arrow.core.EmptyValue
import arrow.core.Ior
import arrow.core.identity
import arrow.typeclasses.Semigroup

@Deprecated(iorDSLDeprecation, ReplaceWith("ior", "arrow.core.raise.ior"))
@Suppress("ClassName")
public object ior {
  @Deprecated(iorDSLDeprecation, ReplaceWith("ior(semigroup, f)", "arrow.core.raise.ior"))
  public inline fun <E, A> eager(
    semigroup: Semigroup<E>,
    crossinline f: suspend IorEagerEffectScope<E>.() -> A
  ): Ior<E, A> =
    eagerEffect<E, Ior<E, A>> {
      val effect = IorEagerEffectScope(semigroup, this)
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      val res = f(effect)
      val leftState = effect.leftState.get()
      if (leftState === EmptyValue) Ior.Right(res) else Ior.Both(EmptyValue.unbox(leftState), res)
    }.fold({ Ior.Left(it) }, ::identity)
  
  @Deprecated(iorDSLDeprecation, ReplaceWith("ior(semigroup, f)", "arrow.core.raise.ior"))
  public suspend inline operator fun <E, A> invoke(
    semigroup: Semigroup<E>,
    crossinline f: suspend IorEffectScope<E>.() -> A
  ): Ior<E, A> =
    effect<E, Ior<E, A>> {
      val effect = IorEffectScope(semigroup, this)
      val res = f(effect)
      val leftState = effect.leftState.get()
      if (leftState === EmptyValue) Ior.Right(res) else Ior.Both(EmptyValue.unbox(leftState), res)
    }.fold({ Ior.Left(it) }, ::identity)
}

@Deprecated(
  "IorEffectScope<E> is replaced with arrow.core.raise.IorRaise<E>",
  ReplaceWith("IorRaise<E>", "arrow.core.raise.IorRaise")
)
public class IorEffectScope<E>(semigroup: Semigroup<E>, private val effect: EffectScope<E>) :
  EffectScope<E>, Semigroup<E> by semigroup {

  @PublishedApi
  internal var leftState: AtomicRef<Any?> = AtomicRef(EmptyValue)

  private fun combine(other: E): E =
    leftState.updateAndGet { state ->
      if (state === EmptyValue) other else EmptyValue.unbox<E>(state).combine(other)
    } as
      E

  public suspend fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> shift(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  override suspend fun <B> shift(r: E): B = effect.shift(combine(r))
}

@Deprecated(
  "IorEagerEffectScope<E> is replaced with arrow.core.raise.IorRaise<E>",
  ReplaceWith("IorRaise<E>", "arrow.core.raise.IorRaise")
)
public class IorEagerEffectScope<E>(semigroup: Semigroup<E>, private val effect: EagerEffectScope<E>) :
  EagerEffectScope<E>, Semigroup<E> by semigroup {

  @PublishedApi
  internal var leftState: AtomicRef<Any?> = AtomicRef(EmptyValue)

  private fun combine(other: E): E =
    leftState.updateAndGet { state ->
      if (state === EmptyValue) other else EmptyValue.unbox<E>(state).combine(other)
    } as
      E

  public suspend fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> shift(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
  override suspend fun <B> shift(r: E): B = effect.shift(combine(r))
}

private const val iorDSLDeprecation =
  "The ior DSL has been moved to arrow.core.raise.ior.\n" +
    "Replace import arrow.core.computations.ior with arrow.core.raise.ior"
