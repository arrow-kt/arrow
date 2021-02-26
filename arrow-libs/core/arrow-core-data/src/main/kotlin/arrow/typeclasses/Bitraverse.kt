package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.KindDeprecation
import arrow.core.identity
import arrow.documented
import arrow.typeclasses.internal.Id
import arrow.typeclasses.internal.fix
import arrow.typeclasses.internal.idApplicative

/**
 * The type class `Bitraverse` defines the behaviour of two separetes `Traverse` over a data type.
 */
@documented
@Deprecated(KindDeprecation)
interface Bitraverse<F> : Bifunctor<F>, Bifoldable<F> {

  fun <G, A, B, C, D> Kind2<F, A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>):
    Kind<G, Kind2<F, C, D>>

  fun <G, A, B> Kind2<F, Kind<G, A>, Kind<G, B>>.bisequence(AP: Applicative<G>): Kind<G, Kind2<F, A, B>> =
    bitraverse(AP, ::identity, ::identity)

  override fun <A, B, C, D> Kind2<F, A, B>.bimap(f: (A) -> C, g: (B) -> D): Kind2<F, C, D> =
    bitraverse(idApplicative, { Id(f(it)) }, { Id(g(it)) }).fix().value
}
