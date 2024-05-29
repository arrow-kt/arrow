package arrow.optics.match

import arrow.core.raise.Raise
import arrow.core.raise.SingletonRaise
import arrow.optics.Optional

public interface MatchScope<S, R> {
  public infix fun <A> Optional<S, A>.then(next: (A) -> R)
  public fun default(next: () -> R)
}

private class MatchScopeImpl<S, R>(val subject: S): MatchScope<S, R> {
  override fun <A> Optional<S, A>.then(next: (A) -> R) {
    this.getOrNull(subject)?.let { throw MatchFound(next(it)) }
  }
  override fun default(next: () -> R) {
    throw MatchFound(next())
  }
}

private class MatchFound(val result: Any?) : Throwable()

@Suppress("UNCHECKED_CAST")
public fun <S, R> S.matchOrElse(
  noMatch: () -> R,
  cases: MatchScope<S, R>.() -> Unit,
): R = try {
  cases(MatchScopeImpl(this))
  noMatch()
} catch (e: MatchFound) {
  e.result as R
}

public class MatchNotFound(public val value: Any?) : Throwable()

public fun <S, R> S.matchOrThrow(
  exception: () -> Throwable = { MatchNotFound(this) },
  cases: MatchScope<S, R>.() -> Unit,
): R = matchOrElse({ throw exception() }, cases)

public fun <S, R> Raise<MatchNotFound>.matchOrRaise(
  value: S,
  cases: MatchScope<S, R>.() -> Unit,
): R = value.matchOrElse({ raise(MatchNotFound(value)) }, cases)

public fun <S, R, E> SingletonRaise<E>.matchOrRaise(
  value: S,
  cases: MatchScope<S, R>.() -> Unit,
): R = value.matchOrElse({ raise() }, cases)

public fun <S> S.matchUnit(
  cases: MatchScope<S, Unit>.() -> Unit,
): Unit = matchOrElse({ }, cases)
