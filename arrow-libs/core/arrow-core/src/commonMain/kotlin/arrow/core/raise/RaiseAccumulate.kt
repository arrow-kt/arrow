@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.EmptyValue
import arrow.core.NonEmptyList
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.emptyCombine
import arrow.core.nel
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * Accumulate the errors from running both [action1] and [action2]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference block: Raise<R>.(A, B) -> C
): C {
  contract {
    callsInPlace(combineError, InvocationKind.AT_MOST_ONCE)
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
  }
  val result1 = either(action1)
  val result2 = either(action2)
  return when (result1) {
    is Either.Right<A> ->
      when (result2) {
        is Either.Right<B> -> block(result1.value, result2.value)
        is Either.Left<R> -> raise(result2.value)
      }
    is Either.Left<R> ->
      when (result2) {
        is Either.Right<B> -> raise(result1.value)
        is Either.Left<R> -> raise(combineError(result1.value, result2.value))
      }
  }
}

/**
 * Accumulate the errors from running [action1], [action2], and [action3]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference block: Raise<R>.(A, B, C) -> D
): D {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2) { x, y -> x to y } },
    action3
  ) { xy, z -> block(xy.first, xy.second, z) }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], and [action4]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference action4: Raise<R>.() -> D,
  @BuilderInference block: Raise<R>.(A, B, C, D) -> E
): E {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2, action3) { x, y, z -> Triple(x, y, z) } },
    action4
  ) { xyz, z -> block(xyz.first, xyz.second, xyz.third, z) }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference action4: Raise<R>.() -> D,
  @BuilderInference action5: Raise<R>.() -> E,
  @BuilderInference block: Raise<R>.(A, B, C, D, E) -> F
): F {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2, action3, action4) { x, y, z, u -> Tuple4(x, y, z, u) } },
    action5
  ) { xyzu, v -> block(xyzu.first, xyzu.second, xyzu.third, xyzu.fourth, v) }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference action4: Raise<R>.() -> D,
  @BuilderInference action5: Raise<R>.() -> E,
  @BuilderInference action6: Raise<R>.() -> F,
  @BuilderInference block: Raise<R>.(A, B, C, D, E, F) -> G
): G {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2, action3, action4, action5) { x, y, z, u, v -> Tuple5(x, y, z, u, v) } },
    action6
  ) { xyzuv, w -> block(xyzuv.first, xyzuv.second, xyzuv.third, xyzuv.fourth, xyzuv.fifth, w) }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference action4: Raise<R>.() -> D,
  @BuilderInference action5: Raise<R>.() -> E,
  @BuilderInference action6: Raise<R>.() -> F,
  @BuilderInference action7: Raise<R>.() -> G,
  @BuilderInference block: Raise<R>.(A, B, C, D, E, F, G) -> H
): H {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2, action3, action4, action5, action6) { x, y, z, u, v, w -> Tuple6(x, y, z, u, v, w) } },
    action7
  ) { xyzuvw, a -> block(xyzuvw.first, xyzuvw.second, xyzuvw.third, xyzuvw.fourth, xyzuvw.fifth, xyzuvw.sixth, a) }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference action4: Raise<R>.() -> D,
  @BuilderInference action5: Raise<R>.() -> E,
  @BuilderInference action6: Raise<R>.() -> F,
  @BuilderInference action7: Raise<R>.() -> G,
  @BuilderInference action8: Raise<R>.() -> H,
  @BuilderInference block: Raise<R>.(A, B, C, D, E, F, G, H) -> I
): I {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2, action3, action4, action5, action6, action7) { x, y, z, u, v, w, a -> Tuple7(x, y, z, u, v, w, a) } },
    action8
  ) { xyzuvwa, b -> block(xyzuvwa.first, xyzuvwa.second, xyzuvwa.third, xyzuvwa.fourth, xyzuvwa.fifth, xyzuvwa.sixth, xyzuvwa.seventh, b) }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9]
 * using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I, J> Raise<R>.zipOrAccumulate(
  combineError: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference action3: Raise<R>.() -> C,
  @BuilderInference action4: Raise<R>.() -> D,
  @BuilderInference action5: Raise<R>.() -> E,
  @BuilderInference action6: Raise<R>.() -> F,
  @BuilderInference action7: Raise<R>.() -> G,
  @BuilderInference action8: Raise<R>.() -> H,
  @BuilderInference action9: Raise<R>.() -> I,
  @BuilderInference block: Raise<R>.(A, B, C, D, E, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combineError,
    { zipOrAccumulate(combineError, action1, action2, action3, action4, action5, action6, action7, action8) { x, y, z, u, v, w, a, b -> Tuple8(x, y, z, u, v, w, a, b) } },
    action9
  ) { xyzuvwab, c -> block(xyzuvwab.first, xyzuvwab.second, xyzuvwab.third, xyzuvwab.fourth, xyzuvwab.fifth, xyzuvwab.sixth, xyzuvwab.seventh, xyzuvwab.eighth, c) }
}

/**
 * Re-raise any errors in [block] in a [NonEmptyList].
 */
@RaiseDSL
public inline fun <R, A> Raise<NonEmptyList<R>>.mapErrorNel(
  crossinline block: Raise<R>.() -> A
): A {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return recover(block) { raise(it.nel()) }
}

/**
 * Accumulate the errors from running both [action1] and [action2].
 */
@RaiseDSL
public inline fun <R, A, B, C> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline block: Raise<R>.(A, B) -> C
): C {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { x, y -> mapErrorNel { block(x, y) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], and [action3].
 */
@RaiseDSL
public inline fun <R, A, B, C, D> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline block: Raise<R>.(A, B, C) -> D
): D {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { x, y, z -> mapErrorNel { block(x, y, z) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], and [action4].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline action4: Raise<R>.() -> D,
  @BuilderInference crossinline block: Raise<R>.(A, B, C, D) -> E
): E {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { mapErrorNel(action4) },
    { x, y, z, u -> mapErrorNel { block(x, y, z, u) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline action4: Raise<R>.() -> D,
  @BuilderInference crossinline action5: Raise<R>.() -> E,
  @BuilderInference crossinline block: Raise<R>.(A, B, C, D, E) -> F
): F {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { mapErrorNel(action4) },
    { mapErrorNel(action5) },
    { x, y, z, u, v -> mapErrorNel { block(x, y, z, u, v) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline action4: Raise<R>.() -> D,
  @BuilderInference crossinline action5: Raise<R>.() -> E,
  @BuilderInference crossinline action6: Raise<R>.() -> F,
  @BuilderInference crossinline block: Raise<R>.(A, B, C, D, E, F) -> G
): G {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { mapErrorNel(action4) },
    { mapErrorNel(action5) },
    { mapErrorNel(action6) },
    { x, y, z, u, v, w -> mapErrorNel { block(x, y, z, u, v, w) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline action4: Raise<R>.() -> D,
  @BuilderInference crossinline action5: Raise<R>.() -> E,
  @BuilderInference crossinline action6: Raise<R>.() -> F,
  @BuilderInference crossinline action7: Raise<R>.() -> G,
  @BuilderInference crossinline block: Raise<R>.(A, B, C, D, E, F, G) -> H
): H {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { mapErrorNel(action4) },
    { mapErrorNel(action5) },
    { mapErrorNel(action6) },
    { mapErrorNel(action7) },
    { x, y, z, u, v, w, a -> mapErrorNel { block(x, y, z, u, v, w, a) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline action4: Raise<R>.() -> D,
  @BuilderInference crossinline action5: Raise<R>.() -> E,
  @BuilderInference crossinline action6: Raise<R>.() -> F,
  @BuilderInference crossinline action7: Raise<R>.() -> G,
  @BuilderInference crossinline action8: Raise<R>.() -> H,
  @BuilderInference crossinline block: Raise<R>.(A, B, C, D, E, F, G, H) -> I
): I {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { mapErrorNel(action4) },
    { mapErrorNel(action5) },
    { mapErrorNel(action6) },
    { mapErrorNel(action7) },
    { mapErrorNel(action8) },
    { x, y, z, u, v, w, a, b -> mapErrorNel { block(x, y, z, u, v, w, a, b) } }
  )
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I, J> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference crossinline action1: Raise<R>.() -> A,
  @BuilderInference crossinline action2: Raise<R>.() -> B,
  @BuilderInference crossinline action3: Raise<R>.() -> C,
  @BuilderInference crossinline action4: Raise<R>.() -> D,
  @BuilderInference crossinline action5: Raise<R>.() -> E,
  @BuilderInference crossinline action6: Raise<R>.() -> F,
  @BuilderInference crossinline action7: Raise<R>.() -> G,
  @BuilderInference crossinline action8: Raise<R>.() -> H,
  @BuilderInference crossinline action9: Raise<R>.() -> I,
  @BuilderInference crossinline block: Raise<R>.(A, B, C, D, E, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action3, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action4, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action5, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action6, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action7, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action8, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action9, InvocationKind.EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    { x, y -> x + y },
    { mapErrorNel(action1) },
    { mapErrorNel(action2) },
    { mapErrorNel(action3) },
    { mapErrorNel(action4) },
    { mapErrorNel(action5) },
    { mapErrorNel(action6) },
    { mapErrorNel(action7) },
    { mapErrorNel(action8) },
    { mapErrorNel(action9) },
    { x, y, z, u, v, w, a, b, c -> mapErrorNel { block(x, y, z, u, v, w, a, b, c) } }
  )
}

/**
 * Accumulate the errors obtained by executing the [block]
 * over every element of [list] using the given [combineError].
 */
@RaiseDSL
public inline fun <R, A, B> Raise<R>.mapOrAccumulate(
  combineError: (R, R) -> R,
  list: Iterable<A>,
  @BuilderInference block: Raise<R>.(A) -> B
): List<B> {
  // this could be implemented using [zipOrAccumulate],
  // but we can have a faster implementation using [buildList]
  var error: Any? = EmptyValue
  val results = buildList {
    list.forEach {
      fold<R, B, Unit>({
        block(it)
      }, { newError ->
        error = emptyCombine(error, newError, combineError)
      }, {
        add(it)
      })
    }
  }
  return when (val e = EmptyValue.unbox<R>(error)) {
    null -> results
    else -> raise(e)
  }
}

/**
 * Accumulate the errors obtained by executing the [block]
 * over every element of [list].
 */
@RaiseDSL
public inline fun <R, A, B> Raise<NonEmptyList<R>>.mapOrAccumulate(
  list: Iterable<A>,
  @BuilderInference crossinline block: Raise<R>.(A) -> B
): List<B> =
  mapOrAccumulate({ x, y -> x + y }, list) { elt -> mapErrorNel { block(elt) } }
