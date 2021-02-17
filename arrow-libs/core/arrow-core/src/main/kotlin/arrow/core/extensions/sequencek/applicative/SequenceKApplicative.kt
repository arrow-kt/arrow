package arrow.core.extensions.sequencek.applicative

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKApplicative
import arrow.typeclasses.Monoid

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: SequenceKApplicative = object :
  arrow.core.extensions.SequenceKApplicative {}

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
fun <A> A.just(): SequenceK<A> = arrow.core.SequenceK.applicative().run {
  this@just.just<A>() as arrow.core.SequenceK<A>
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
fun unit(): SequenceK<Unit> = arrow.core.SequenceK
  .applicative()
  .unit() as arrow.core.SequenceK<kotlin.Unit>

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
fun <A, B> Kind<ForSequenceK, A>.map(arg1: Function1<A, B>): SequenceK<B> =
  arrow.core.SequenceK.applicative().run {
    this@map.map<A, B>(arg1) as arrow.core.SequenceK<B>
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
fun <A> Kind<ForSequenceK, A>.replicate(arg1: Int): SequenceK<List<A>> =
  arrow.core.SequenceK.applicative().run {
    this@replicate.replicate<A>(arg1) as arrow.core.SequenceK<kotlin.collections.List<A>>
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
fun <A> Kind<ForSequenceK, A>.replicate(arg1: Int, arg2: Monoid<A>): SequenceK<A> =
  arrow.core.SequenceK.applicative().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.core.SequenceK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Applicative typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.applicative(): SequenceKApplicative = applicative_singleton
