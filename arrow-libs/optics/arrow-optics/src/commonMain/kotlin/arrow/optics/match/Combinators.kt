package arrow.optics.match

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.optics.Optional

public fun <S, A> Optional<S, A>.suchThat(
  predicate: (A) -> Boolean,
): Optional<S, A> = this.compose(predicate(predicate))

public fun <A> predicate(
  predicate: (A) -> Boolean,
): Optional<A, A> = Optional(
  getOrModify = { x ->
    either {
      ensure(predicate(x)) { x }
      x
    }
  },
  set = { x, _ -> x }
)

public fun <S, A> Optional<S, A>.ifEquals(value: A): Optional<S, A> = this.suchThat { it == value }

public fun <A> equalsTo(value: A): Optional<A, A> = predicate { it == value }

public operator fun <T, S, A> Optional<T, S>.invoke(
  p1: Optional<S, A>,
): Optional<T, A> = this.compose(p1)

public operator fun <T, S, A, B> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>,
): Optional<T, Pair<A, B>> = this.compose(combine(p1, p2))

public operator fun <T, S, A, B, C> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>,
): Optional<T, Triple<A, B, C>> = this.compose(combine(p1, p2, p3))

public operator fun <T, S, A, B, C, D> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>, p4: Optional<S, D>,
): Optional<T, Tuple4<A, B, C, D>> = this.compose(combine(p1, p2, p3, p4))

public operator fun <T, S, A, B, C, D, E> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>, p4: Optional<S, D>, p5: Optional<S, E>
): Optional<T, Tuple5<A, B, C, D, E>> = this.compose(combine(p1, p2, p3, p4, p5))

private fun <S, A> OptionalUsingRaise(
  getOrModify: Raise<S>.(S) -> A,
  set: (S, A) -> S
): Optional<S, A> = Optional(
  getOrModify = { x -> either { getOrModify(x) } },
  set = set
)

private fun <S, A, B> combine(
  p1: Optional<S, A>, p2: Optional<S, B>,
): Optional<S, Pair<A, B>> = OptionalUsingRaise(
  { Pair(p1.getOrModify(it).bind(), p2.getOrModify(it).bind()) },
  { x, (a, b) -> p2.set(p1.set(x, a), b) }
)

private fun <S, A, B, C> combine(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>,
): Optional<S, Triple<A, B, C>> = OptionalUsingRaise(
  { Triple(p1.getOrModify(it).bind(), p2.getOrModify(it).bind(), p3.getOrModify(it).bind()) },
  { x, (a, b, c) -> p3.set(p2.set(p1.set(x, a), b), c) }
)

private fun <S, A, B, C, D> combine(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>, p4: Optional<S, D>,
): Optional<S, Tuple4<A, B, C, D>> = OptionalUsingRaise(
  { Tuple4(
    p1.getOrModify(it).bind(),
    p2.getOrModify(it).bind(),
    p3.getOrModify(it).bind(),
    p4.getOrModify(it).bind())
  },
  { x, (a, b, c, d) -> p4.set(p3.set(p2.set(p1.set(x, a), b), c), d) }
)

private fun <S, A, B, C, D, E> combine(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>, p4: Optional<S, D>, p5: Optional<S, E>,
): Optional<S, Tuple5<A, B, C, D, E>> = OptionalUsingRaise(
  { Tuple5(
    p1.getOrModify(it).bind(),
    p2.getOrModify(it).bind(),
    p3.getOrModify(it).bind(),
    p4.getOrModify(it).bind(),
    p5.getOrModify(it).bind())
  },
  { x, (a, b, c, d, e) -> p5.set(p4.set(p3.set(p2.set(p1.set(x, a), b), c), d), e) }
)
