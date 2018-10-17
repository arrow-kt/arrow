package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.identity

interface Bifunctor<F> {
  fun <A, B, C, D> Kind2<F, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<F, C, D>

  fun <A, B, C> Kind2<F, A, B>.mapLeft(f: (A) -> C): Kind2<F, C, B> =
    bimap(f, ::identity)

  fun <X> rightFunctor(): Functor<Kind<F, X>> = object : RightFunctor<F, X> {
    override val F: Bifunctor<F> = this@Bifunctor
  }

  fun <X> leftFunctor(): Functor<Conested<F, X>> = CocomposedFunctor(this@Bifunctor)

  fun <AA, B, A : AA> Kind2<F, A, B>.leftWiden(): Kind2<F, AA, B> = this
}

private interface RightFunctor<F, X> : Functor<Kind<F, X>> {
  val F: Bifunctor<F>

  override fun <A, B> Kind2<F, X, A>.map(f: (A) -> B): Kind2<F, X, B> =
    F.run { bimap(::identity, f) }
}
