package arrow.optics.match

import arrow.core.Tuple4
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.optics.Optional

public interface MatchScope<S, R> {
  public infix fun <A> Optional<S, A>.then(next: (A) -> R)
  public fun default(next: () -> R)
}

private class MatchFound(val result: Any?) : Throwable()

@Suppress("UNCHECKED_CAST")
public fun <S, R> S.match(cases: MatchScope<S, R>.() -> Unit): R {
  val subject = this
  val scope = object : MatchScope<S, R> {
    override fun <A> Optional<S, A>.then(next: (A) -> R) {
      this.getOrNull(subject)?.let { throw MatchFound(next(it)) }
    }
    override fun default(next: () -> R) {
      throw MatchFound(next())
    }
  }
  try {
    cases(scope)
    throw IllegalArgumentException("no match found")
  } catch (e: MatchFound) {
    return e.result as R
  }
}

public operator fun <T, S, A> Optional<T, S>.invoke(
  p1: Optional<S, A>
): Optional<T, A> = this.compose(p1)

public operator fun <T, S, A, B> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>
): Optional<T, Pair<A, B>> = this.compose(combine(p1, p2))

public operator fun <T, S, A, B, C> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>
): Optional<T, Triple<A, B, C>> = this.compose(combine(p1, p2, p3))

public operator fun <T, S, A, B, C, D> Optional<T, S>.invoke(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>, p4: Optional<S, D>
): Optional<T, Tuple4<A, B, C, D>> = this.compose(combine(p1, p2, p3, p4))

public fun <S, A> Optional<S, A>.suchThat(
  predicate: (A) -> Boolean
): Optional<S, A> = this.compose(predicate(predicate))

public fun <A> predicate(
  predicate: (A) -> Boolean
): Optional<A, A> = Optional(
  getOrModify = { x -> either {
    ensure(predicate(x)) { x }
    x
  } },
  set = { x, _ -> x }
)

public fun <S, A> Optional<S, A>.ifEquals(value: A): Optional<S, A> = this.suchThat { it == value }

public fun <A> equalsTo(value: A): Optional<A, A> = predicate { it == value }

private fun <S, A, B> combine(
  p1: Optional<S, A>, p2: Optional<S, B>
): Optional<S, Pair<A, B>> = Optional(
  getOrModify = { x -> either {
    Pair(p1.getOrModify(x).bind(), p2.getOrModify(x).bind())
  } },
  set = { x, (a, b) -> p2.set(p1.set(x, a), b) }
)

private fun <S, A, B, C> combine(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>
): Optional<S, Triple<A, B, C>> = Optional(
  getOrModify = { x -> either {
    Triple(p1.getOrModify(x).bind(), p2.getOrModify(x).bind(), p3.getOrModify(x).bind())
  } },
  set = { x, (a, b, c) -> p3.set(p2.set(p1.set(x, a), b), c) }
)

private fun <S, A, B, C, D> combine(
  p1: Optional<S, A>, p2: Optional<S, B>, p3: Optional<S, C>, p4: Optional<S, D>
): Optional<S, Tuple4<A, B, C, D>> = Optional(
  getOrModify = { x -> either {
    Tuple4(p1.getOrModify(x).bind(), p2.getOrModify(x).bind(), p3.getOrModify(x).bind(), p4.getOrModify(x).bind())
  } },
  set = { x, (a, b, c, d) -> p4.set(p3.set(p2.set(p1.set(x, a), b), c), d) }
)
