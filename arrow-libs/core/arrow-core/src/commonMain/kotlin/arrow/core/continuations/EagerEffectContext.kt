package arrow.core.continuations

import arrow.core.EmptyValue
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
public interface EagerEffectContext<R> {

  /** Short-circuit the [Effect] computation with value [R]. */
  public suspend fun <B> shift(r: R): B

  public suspend fun <A> EagerEffect<R, A>.bind(): A {
    var left: Any? = EmptyValue
    var right: Any? = EmptyValue
    fold({ r -> left = r }, { a -> right = a })
    return if (left === EmptyValue) EmptyValue.unbox(right) else shift(EmptyValue.unbox(left))
  }
}

