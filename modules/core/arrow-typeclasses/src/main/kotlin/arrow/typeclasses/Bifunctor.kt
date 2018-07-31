package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.identity

interface Bifunctor<F> {
  fun <A, B, C, D> Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>

  fun <A, B, C> Kind2<F, A, B>.mapLeft(f: (A) -> C): Kind2<F, C, B> = bimap(f, ::identity)

  fun <X> leftFunctor(): Functor<Kind<F, X>> = object : LeftFunctor<F, X>() {
    override val F: Bifunctor<F> = this@Bifunctor
  }

}

private abstract class LeftFunctor<F, X> : Functor<Kind<F, X>> {
  abstract val F: Bifunctor<F>

  override fun <A, B> Kind<Kind<F, X>, A>.map(f: (A) -> B): Kind<Kind<F, X>, B> {
    return F.run { bimap(f, ::identity) }
  }
}
