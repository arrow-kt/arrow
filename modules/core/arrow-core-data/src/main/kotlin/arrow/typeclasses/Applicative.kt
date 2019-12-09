@file:Suppress("UNUSED_PARAMETER")

package arrow.typeclasses

import arrow.Kind

/**
 * ank_macro_hierarchy(arrow.typeclasses.Applicative)
 */
interface Applicative<F> : Apply<F> {

  fun <A> just(a: A): Kind<F, A>

  fun <A> A.just(dummy: Unit = Unit): Kind<F, A> =
    just(this)

  fun unit(): Kind<F, Unit> = just(Unit)

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    ap(just(f))

  fun <A> Kind<F, A>.replicate(n: Int): Kind<F, List<A>> =
    if (n <= 0) just(emptyList())
    else map(this, replicate(n - 1)) { (a, xs) -> listOf(a) + xs }

  fun <A> Kind<F, A>.replicate(n: Int, MA: Monoid<A>): Kind<F, A> =
    if (n <= 0) just(MA.empty())
    else map(this@replicate, replicate(n - 1, MA)) { (a, xs) -> MA.run { a + xs } }
}
