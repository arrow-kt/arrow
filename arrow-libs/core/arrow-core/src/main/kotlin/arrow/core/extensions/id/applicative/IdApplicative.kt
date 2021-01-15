package arrow.core.extensions.id.applicative

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdApplicative
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
internal val applicative_singleton: IdApplicative = object : arrow.core.extensions.IdApplicative {}

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
  "just()",
  "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <A> A.just(): Id<A> = arrow.core.Id.applicative().run {
  this@just.just<A>() as arrow.core.Id<A>
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
  "unit()",
  "arrow.core.Id.unit"
  ),
  DeprecationLevel.WARNING
)
fun unit(): Id<Unit> = arrow.core.Id
   .applicative()
   .unit() as arrow.core.Id<kotlin.Unit>

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
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForId, A>.map(arg1: Function1<A, B>): Id<B> = arrow.core.Id.applicative().run {
  this@map.map<A, B>(arg1) as arrow.core.Id<B>
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
  "replicate(arg1)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForId, A>.replicate(arg1: Int): Id<List<A>> = arrow.core.Id.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.core.Id<kotlin.collections.List<A>>
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
  "replicate(arg1, arg2)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForId, A>.replicate(arg1: Int, arg2: Monoid<A>): Id<A> =
    arrow.core.Id.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Id<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.applicative(): IdApplicative = applicative_singleton
