@file:JvmMultifileClass
@file:JvmName("ControlKt")
package arrow.core.coroutines

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

import arrow.continuations.generic.AtomicRef
import arrow.continuations.generic.updateAndGet
import arrow.core.EmptyValue
import arrow.core.Ior
import arrow.core.identity
import arrow.typeclasses.Semigroup

public suspend fun <E, A> ior(semigroup: Semigroup<E>, f: suspend IorEffect<E>.() -> A): Ior<E, A> =
  control<E, Ior<E, A>> {
    val effect = IorEffect(semigroup, this)
    val res = f(effect)
    val leftState = effect.leftState.get()
    if (leftState === EmptyValue) Ior.Right(res) else Ior.Both(EmptyValue.unbox(leftState), res)
  }
    .fold({ Ior.Left(it) }, ::identity)

public class IorEffect<E>(semigroup: Semigroup<E>, private val cont: ControlEffect<E>) :
  ControlEffect<E>, Semigroup<E> by semigroup {

  internal var leftState: AtomicRef<Any?> = AtomicRef(EmptyValue)

  private fun combine(other: E): E =
    leftState.updateAndGet { state ->
      if (state === EmptyValue) other else EmptyValue.unbox<E>(state).combine(other)
    } as E

  public suspend fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> shift(value)
      is Ior.Right -> value
      is Ior.Both -> {
        combine(leftValue)
        rightValue
      }
    }

  override suspend fun <B> shift(r: E): B = cont.shift(combine(r))
}
