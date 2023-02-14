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
import arrow.typeclasses.Semigroup
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * Accumulate the errors from running both [action1] and [action2]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: Raise<R>.() -> A,
  @BuilderInference action2: Raise<R>.() -> B,
  @BuilderInference block: Raise<R>.(A, B) -> C
): C {
  contract {
    callsInPlace(action1, InvocationKind.EXACTLY_ONCE)
    callsInPlace(action2, InvocationKind.EXACTLY_ONCE)
  }
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2)
  ) { a, b -> block(a, b) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], and [action3]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3)
  ) { a, b, c -> block(a, b, c) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], and [action4]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3),
    either(action4)
  ) { a, b, c, d -> block(a, b, c, d) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5)
  ) { a, b, c, d, e -> block(a, b, c, d, e) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6)
  ) { a, b, c, d, e, f -> block(a, b, c, d, e, f) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6),
    either(action7)
  ) { a, b, c, d, e, f, g -> block(a, b, c, d, e, f, g) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6),
    either(action7),
    either(action8)
  ) { a, b, c, d, e, f, g, h -> block(a, b, c, d, e, f, g, h) }.bind()
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9]
 * using the given [combine].
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I, J> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
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
  return Either.zipOrAccumulate(
    combine,
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6),
    either(action7),
    either(action8),
    either(action9)
  ) { a, b, c, d, e, f, g, h, i -> block(a, b, c, d, e, f, g, h, i) }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2)
  ) { a, b -> mapErrorNel { block(a, b) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
  ) { a, b, c -> mapErrorNel { block(a, b, c) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
    either(action4)
  ) { a, b, c, d -> mapErrorNel { block(a, b, c, d) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5)
  ) { a, b, c, d, e -> mapErrorNel { block(a, b, c, d, e) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6)
  ) { a, b, c, d, e, f -> mapErrorNel { block(a, b, c, d, e, f) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6),
    either(action7)
  ) { a, b, c, d, e, f, g -> mapErrorNel { block(a, b, c, d, e, f, g) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6),
    either(action7),
    either(action8)
  ) { a, b, c, d, e, f, g, h -> mapErrorNel { block(a, b, c, d, e, f, g, h) } }.bind()
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
  return Either.zipOrAccumulate(
    either(action1),
    either(action2),
    either(action3),
    either(action4),
    either(action5),
    either(action6),
    either(action7),
    either(action8),
    either(action9)
  ) { x, y, z, u, v, w, a, b, c -> mapErrorNel { block(x, y, z, u, v, w, a, b, c) } }.bind()
}

/**
 * Accumulate the errors obtained by executing the [block]
 * over every element of [list] using the given [semigroup].
 */
@RaiseDSL
public inline fun <R, A, B> Raise<R>.mapOrAccumulate(
  semigroup: Semigroup<@UnsafeVariance R>,
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
        error = semigroup.emptyCombine(error, newError)
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
  mapOrAccumulate(Semigroup.nonEmptyList(), list) { elt -> mapErrorNel { block(elt) } }
