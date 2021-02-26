package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Tuple2
import arrow.core.identity

@Deprecated(KindDeprecation)
/**
 * The `Unzip` typeclass extends `Zip` by providing an inverse operation to zip.
 */
interface Unzip<F> : Zip<F> {

  fun <A, B> Kind<F, Tuple2<A, B>>.unzip(): Tuple2<Kind<F, A>, Kind<F, B>> =
    unzipWith(::identity)

  fun <A, B, C> Kind<F, C>.unzipWith(fc: (C) -> Tuple2<A, B>): Tuple2<Kind<F, A>, Kind<F, B>> =
    map(fc).unzip()
}
