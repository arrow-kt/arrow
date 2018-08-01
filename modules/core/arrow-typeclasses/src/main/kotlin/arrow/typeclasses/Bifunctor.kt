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

  fun <G, A, B> compose(G0: Bifunctor<G>): Bifunctor<Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>> =
      object : ComposedBifunctor<F, G, A, B> {
          override val F: Bifunctor<F> = this@Bifunctor
          override val G: Bifunctor<G> = G0
      }

  fun <AA, B, A : AA> Kind2<F, A, B>.leftWiden(): Kind2<F, AA, B> = this
}

private interface RightFunctor<F, X> : Functor<Kind<F, X>> {
  val F: Bifunctor<F>

  override fun <A, B> Kind2<F, X, A>.map(f: (A) -> B): Kind2<F, X, B> =
    F.run { bimap(::identity, f) }
}

private interface ComposedBifunctor<F, G, A, B> : Bifunctor<Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>> {
    val F: Bifunctor<F>
    val G: Bifunctor<G>

    override fun <A, B, C, D> Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>.bimap(
        fl: (A) -> C,
        fr: (B) -> D
    ): Kind2<F, Kind2<G, C, D>, Kind2<G, C, D>> {
        val innerBimap: (Kind2<G, A, B>) -> Kind2<G, C, D> = { G.run { it.bimap(fl, fr) } }
        return F.run { bimap(innerBimap, innerBimap) }
    }
}