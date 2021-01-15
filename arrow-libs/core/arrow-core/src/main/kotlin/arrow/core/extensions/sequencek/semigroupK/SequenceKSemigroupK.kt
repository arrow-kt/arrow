package arrow.core.extensions.sequencek.semigroupK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: SequenceKSemigroupK = object :
    arrow.core.extensions.SequenceKSemigroupK {}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineK(arg1)",
  "arrow.core.combineK"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.combineK(arg1: Kind<ForSequenceK, A>): SequenceK<A> =
    arrow.core.SequenceK.semigroupK().run {
  this@combineK.combineK<A>(arg1) as arrow.core.SequenceK<A>
}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "algebra()",
  "arrow.core.SequenceK.algebra"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Semigroup<Kind<ForSequenceK, A>> = arrow.core.SequenceK
   .semigroupK()
   .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForSequenceK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.semigroupK(): SequenceKSemigroupK = semigroupK_singleton
