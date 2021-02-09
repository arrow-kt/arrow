package arrow.core.extensions.sequence.applicative

import arrow.core.extensions.SequenceKApplicative
import arrow.typeclasses.Monoid
import kotlin.sequences.Sequence

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "sequenceOf(this)"
  ),
  DeprecationLevel.WARNING
)
fun <A> A.just(): Sequence<A> =
    arrow.core.extensions.sequence.applicative.Sequence.applicative().run {
  this@just.just<A>() as kotlin.sequences.Sequence<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "sequenceOf(Unit)"
  ),
  DeprecationLevel.WARNING
)
fun unit(): Sequence<Unit> = arrow.core.extensions.sequence.applicative.Sequence
   .applicative()
   .unit() as kotlin.sequences.Sequence<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.map(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.map(arg1: Function1<A, B>): Sequence<B> =
    arrow.core.extensions.sequence.applicative.Sequence.applicative().run {
  arrow.core.SequenceK(this@map).map<A, B>(arg1) as kotlin.sequences.Sequence<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.replicate(arg1)",
    "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.replicate(arg1: Int): Sequence<List<A>> =
    arrow.core.extensions.sequence.applicative.Sequence.applicative().run {
  arrow.core.SequenceK(this@replicate).replicate<A>(arg1) as
    kotlin.sequences.Sequence<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.replicate(arg1, arg2)",
    "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.replicate(arg1: Int, arg2: Monoid<A>): Sequence<A> =
    arrow.core.extensions.sequence.applicative.Sequence.applicative().run {
  arrow.core.SequenceK(this@replicate).replicate<A>(arg1, arg2) as kotlin.sequences.Sequence<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: SequenceKApplicative = object :
    arrow.core.extensions.SequenceKApplicative {}

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
    "Applicative typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun applicative(): SequenceKApplicative = applicative_singleton}
