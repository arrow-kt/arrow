package arrow.core.extensions.either.bitraverse

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherBitraverse
import arrow.core.fix
import arrow.typeclasses.Applicative
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bitraverse_singleton: EitherBitraverse = object :
  arrow.core.extensions.EitherBitraverse {}

@JvmName("bitraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with bitraverse or bitraverseValidated from arrow.core.*")
fun <G, A, B, C, D> Kind<Kind<ForEither, A>, B>.bitraverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, C>>,
  arg3: Function1<B, Kind<G, D>>
): Kind<G, Kind<Kind<ForEither, C>, D>> = arrow.core.Either.bitraverse().run {
  this@bitraverse.bitraverse<G, A, B, C, D>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForEither, C>, D>>
}

@JvmName("bisequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with bisequence or bisequenceValidated from arrow.core.*")
fun <G, A, B> Kind<Kind<ForEither, Kind<G, A>>, Kind<G, B>>.bisequence(arg1: Applicative<G>):
  Kind<G, Kind<Kind<ForEither, A>, B>> = arrow.core.Either.bitraverse().run {
  this@bisequence.bisequence<G, A, B>(arg1) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForEither, A>, B>>
}

@JvmName("bimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("bimap(arg1, arg2)"))
fun <A, B, C, D> Kind<Kind<ForEither, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>):
  Either<C, D> =
  fix().bimap(arg1, arg2)

/**
 *  The type class `Bitraverse` defines the behaviour of two separetes `Traverse` over a data type.
 *
 *  Every instance of `Bitraverse<F>` must contains the next functions:
 *
 *  ## Bitraverse
 *
 *  `Bitraverse` perfoms a`Traverse` over both side of the Data type which is `Bifoldable`.
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 *  import arrow.core.extensions.option.applicative.applicative
 *  import arrow.core.extensions.*
 *  import arrow.core.extensions.tuple2.bitraverse.bitraverse
 *  fun main() {
 *  //sampleStart
 *  val f: (Int) -> Option<Int> = { Some(it + 1) }
 *  val g: (Int) -> Option<Int> = { Some(it * 3) }
 *
 *  val tuple = Tuple2(1, 2)
 *  val bitraverseResult = tuple.bitraverse(Option.applicative(), f, g)
 *  //sampleEnd
 *  println(bitraverseResult)
 *  }
 *  ```
 *
 *  ## Bisequence
 *
 *  `Bisequence` invert the original structure `F<G<A,B>>` to `G<F<A>,F<B>>`
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 *  import arrow.core.extensions.*
 *  import arrow.core.extensions.option.applicative.applicative
 *  import arrow.core.extensions.tuple2.bitraverse.bisequence
 *  fun main() {
 *  //sampleStart
 *  val tuple = Tuple2(Some(1), Some(2))
 *  val sequenceResult = tuple.bisequence(Option.applicative())
 *  //sampleEnd
 *  println(sequenceResult)
 *  }
 *  ```
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on Either")
inline fun Companion.bitraverse(): EitherBitraverse = bitraverse_singleton
