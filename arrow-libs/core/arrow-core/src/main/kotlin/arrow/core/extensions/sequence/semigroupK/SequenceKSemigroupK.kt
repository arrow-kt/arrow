package arrow.core.extensions.sequence.semigroupK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.extensions.SequenceKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.sequences.Sequence

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
    "this + arg1"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.combineK(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.semigroupK.Sequence.semigroupK().run {
  arrow.core.SequenceK(this@combineK).combineK<A>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
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
    "Semigroup.sequence<A>()",
    "arrow.core.sequence", "arrow.typeclasses.Semigroup"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Semigroup<Kind<ForSequenceK, A>> =
    arrow.core.extensions.sequence.semigroupK.Sequence
   .semigroupK()
   .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForSequenceK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: SequenceKSemigroupK = object :
    arrow.core.extensions.SequenceKSemigroupK {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "SemigroupK typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun semigroupK(): SequenceKSemigroupK = semigroupK_singleton}
