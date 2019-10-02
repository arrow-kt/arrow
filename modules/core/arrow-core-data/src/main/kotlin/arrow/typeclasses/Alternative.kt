package arrow.typeclasses

import arrow.Kind
import arrow.core.ListK
import arrow.core.k

/**
 * ank_macro_hierarchy(arrow.typeclasses.Alternative)
 */
interface Alternative<F> : Applicative<F>, MonoidK<F> {
  fun <A> Kind<F, A>.some(): Kind<F, ListK<A>> = map(this, many()) { (v, vs) -> (listOf(v) + vs).k() }

  fun <A> Kind<F, A>.many(): Kind<F, ListK<A>> = some().orElse(just(emptyList<A>().k()))

  infix fun <A> Kind<F, A>.alt(b: Kind<F, A>): Kind<F, A> = this.orElse(b)

  fun <A> Kind<F, A>.orElse(b: Kind<F, A>): Kind<F, A>
}
