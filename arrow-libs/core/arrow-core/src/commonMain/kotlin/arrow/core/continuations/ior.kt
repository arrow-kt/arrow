package arrow.core.continuations

import arrow.continuations.generic.AtomicRef
import arrow.continuations.generic.updateAndGet
import arrow.core.EmptyValue
import arrow.core.Ior
import arrow.core.identity
import arrow.typeclasses.Semigroup

@Suppress("ClassName")
public object ior {
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
