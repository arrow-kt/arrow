package arrow.match

import arrow.core.Tuple4
import arrow.core.Tuple5
import kotlin.reflect.KClass
import arrow.core.raise.Raise
import arrow.core.raise.SingletonRaise

public abstract class MatchScope<S, R> {
  public abstract infix fun <A> Matcher<S, A>.then(next: (A) -> R)
  public abstract fun default(next: () -> R)

  public val it: Matcher<S, S> = Matcher("it") { it }

  public inline fun <reified A: S & Any, B> KClass<A>.of(
    field: Matcher<A, B>
  ): Matcher<S, B> = instanceOf<S, A>().of(field)

  public inline fun <reified A: S & Any, B, C> KClass<A>.of(
    field1: Matcher<A, B>,
    field2: Matcher<A, C>
  ): Matcher<S, Pair<B, C>> = instanceOf<S, A>().of(field1, field2)

  public inline fun <reified A: S & Any, B, C, D> KClass<A>.of(
    field1: Matcher<A, B>,
    field2: Matcher<A, C>,
    field3: Matcher<A, D>
  ): Matcher<S, Triple<B, C, D>> = instanceOf<S, A>().of(field1, field2, field3)

  public inline fun <reified A: S & Any, B, C, D, E> KClass<A>.of(
    field1: Matcher<A, B>,
    field2: Matcher<A, C>,
    field3: Matcher<A, D>,
    field4: Matcher<A, E>
  ): Matcher<S, Tuple4<B, C, D, E>> = instanceOf<S, A>().of(field1, field2, field3, field4)

  public inline fun <reified A: S & Any, B, C, D, E, F> KClass<A>.of(
    field1: Matcher<A, B>,
    field2: Matcher<A, C>,
    field3: Matcher<A, D>,
    field4: Matcher<A, E>,
    field5: Matcher<A, F>
  ): Matcher<S, Tuple5<B, C, D, E, F>> = instanceOf<S, A>().of(field1, field2, field3, field4, field5)
}

private class MatchScopeImpl<S, R>(val subject: S): MatchScope<S, R>() {
  override fun <A> Matcher<S, A>.then(next: (A) -> R) {
    try {
      this.get(subject)?.let { throw MatchFound(next(it)) }
    } catch (e: DoesNotMatch) {
      /* do nothing */
    }
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
