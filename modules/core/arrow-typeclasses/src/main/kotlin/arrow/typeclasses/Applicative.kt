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
}
