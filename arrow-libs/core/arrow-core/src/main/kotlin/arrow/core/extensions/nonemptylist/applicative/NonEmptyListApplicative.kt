package arrow.core.extensions.nonemptylist.applicative

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListApplicative
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: NonEmptyListApplicative = object :
  arrow.core.extensions.NonEmptyListApplicative {}

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
    "NonEmptyList.just(this)",
    "arrow.core.NonEmptyList",
    "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <A> A.just(): NonEmptyList<A> = arrow.core.NonEmptyList.applicative().run {
  this@just.just<A>() as arrow.core.NonEmptyList<A>
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
    "NonEmptyList.just(Unit)",
    "arrow.core.NonEmptyList",
    "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun unit(): NonEmptyList<Unit> = arrow.core.NonEmptyList
  .applicative()
  .unit() as arrow.core.NonEmptyList<kotlin.Unit>

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
    "fix().map<B>(arg1)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.map(arg1: Function1<A, B>): NonEmptyList<B> =
  arrow.core.NonEmptyList.applicative().run {
    this@map.map<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
    "fix().replicate<A>(arg1)",
    "arrow.core.fix",
    "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.replicate(arg1: Int): NonEmptyList<List<A>> =
  arrow.core.NonEmptyList.applicative().run {
    this@replicate.replicate<A>(arg1) as arrow.core.NonEmptyList<kotlin.collections.List<A>>
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
    "fix().replicate<A>(arg1, arg2)",
    "arrow.core.fix",
    "arrow.core.replicate"
  )
)
fun <A> Kind<ForNonEmptyList, A>.replicate(arg1: Int, arg2: Monoid<A>): NonEmptyList<A> =
  arrow.core.NonEmptyList.applicative().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.core.NonEmptyList<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Applicative typeclass is deprecated. Use concrete methods on NonEmptyList",
  level = DeprecationLevel.WARNING
)
inline fun Companion.applicative(): NonEmptyListApplicative = applicative_singleton
