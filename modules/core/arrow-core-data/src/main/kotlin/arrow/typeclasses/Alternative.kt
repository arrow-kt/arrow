package arrow.typeclasses

import arrow.Kind
import arrow.core.SequenceK
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
  fun <A> Kind<F, A>.some(): Kind<F, SequenceK<A>> = map(this, many()) { (v, vs) -> (sequenceOf(v) + vs).k() }

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
}
