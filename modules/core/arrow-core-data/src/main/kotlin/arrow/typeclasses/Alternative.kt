package arrow.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.None
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.Some
import arrow.core.fix
import arrow.core.k

/**
 * ank_macro_hierarchy(arrow.typeclasses.Alternative)
 *
 * The Alternative type class is for Applicative functors which also have a monoid structure.
 *
 * @see <a href="http://arrow-kt.io/docs/arrow/typeclasses/alternative/">Alternative documentation</a>
 */
interface Alternative<F> : Applicative<F>, MonoidK<F> {
  /**
   * Repeats the computation until it fails. Requires it to succeed at least once.
   *
   * @receiver computation to repeat.
   * @returns the collection of results with at least 1 repetition.
   */
  fun <A> Kind<F, A>.some(): Kind<F, SequenceK<A>> = lazyAp { many().map { { a: A -> (sequenceOf(a) + it).k() } } }

  /**
   * Repeats the computation until it fails. Does not requires it to succeed.
   *
   * @receiver computation to repeat.
   * @returns the collection of results.
   */
  fun <A> Kind<F, A>.many(): Kind<F, SequenceK<A>> = some().orElse(just(emptySequence<A>().k()))

  /**
   * Combines two computations.
   * Infix alias over [orElse].
   *
   * @receiver computation to combine with [b].
   * @param b computation to combine with [this@orElse].
   * @returns a combination of both computations.
   */
  infix fun <A> Kind<F, A>.alt(b: Kind<F, A>): Kind<F, A> = this.orElse(b)

  /**
   * Combines two computations.
   *
   * @receiver computation to combine with [b].
   * @param b computation to combine with [this@orElse].
   * @returns a combination of both computations.
   */
  fun <A> Kind<F, A>.orElse(b: Kind<F, A>): Kind<F, A>

  override fun <A> Kind<F, A>.combineK(y: Kind<F, A>): Kind<F, A> = orElse(y)

  /**
   * Wraps the result in an optional. This never fails.
   * @receiver computation to execute
   * @return Option with either the result or none in case it failed
   */
  fun <A> Kind<F, A>.optional(): Kind<F, Option<A>> = map(::Some).orElse(just(None))

  /**
   * Lazy or else, useful when traversing a structure with asum which short circuits on success. In general this should be implemented
   *  for every Alternative that models success and failure.
   */
  fun <A> Kind<F, A>.lazyOrElse(b: () -> Kind<F, A>): Kind<F, A> = orElse(b())

  fun <T, A> Kind<T, Kind<F, A>>.asum(FT: Foldable<T>): Kind<F, A> = FT.run {
    foldRight(Eval.now(empty<A>())) { v, acc ->
      Eval.later { v.lazyOrElse { acc.value() } }
    }.value()
  }

  fun <T, A> Kind<T, A>.afold(FT: Foldable<T>): Kind<F, A> = FT.run { toList().afromList() }

  fun <A> List<A>.afromList(): Kind<F, A> = map { just(it) }.k().asum(object: Foldable<ForListK> {
    override fun <A, B> Kind<ForListK, A>.foldLeft(b: B, f: (B, A) -> B): B = fix().foldLeft(b, f)
    override fun <A, B> Kind<ForListK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = fix().foldRight(lb, f)
  })

  fun <A> Option<A>.afromOption(): Kind<F, A> = fold({ empty() }, { just(it) })
}
