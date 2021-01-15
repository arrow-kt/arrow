package arrow.core.extensions.andthen.applicative

import arrow.Kind
import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.ForAndThen
import arrow.core.extensions.AndThenApplicative
import arrow.typeclasses.Monoid
import kotlin.Any
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
internal val applicative_singleton: AndThenApplicative<Any?> = object : AndThenApplicative<Any?> {}

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
fun <X, A> A.just(): AndThen<X, A> = arrow.core.AndThen.applicative<X>().run {
  this@just.just<A>() as arrow.core.AndThen<X, A>
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
  "arrow.core.AndThen.unit"
  ),
  DeprecationLevel.WARNING
)
fun <X> unit(): AndThen<X, Unit> = arrow.core.AndThen
   .applicative<X>()
   .unit() as arrow.core.AndThen<X, kotlin.Unit>

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
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.map(arg1: Function1<A, B>): AndThen<X, B> =
    arrow.core.AndThen.applicative<X>().run {
  this@map.map<A, B>(arg1) as arrow.core.AndThen<X, B>
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
fun <X, A> Kind<Kind<ForAndThen, X>, A>.replicate(arg1: Int): AndThen<X, List<A>> =
    arrow.core.AndThen.applicative<X>().run {
  this@replicate.replicate<A>(arg1) as arrow.core.AndThen<X, kotlin.collections.List<A>>
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
fun <X, A> Kind<Kind<ForAndThen, X>, A>.replicate(arg1: Int, arg2: Monoid<A>): AndThen<X, A> =
    arrow.core.AndThen.applicative<X>().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.AndThen<X, A>
}

@JvmName("just")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "just(a)",
  "arrow.core.AndThen.just"
  ),
  DeprecationLevel.WARNING
)
fun <X, A> just(a: A): AndThen<X, A> = arrow.core.AndThen
   .applicative<X>()
   .just<A>(a) as arrow.core.AndThen<X, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <X> Companion.applicative(): AndThenApplicative<X> = applicative_singleton as
    arrow.core.extensions.AndThenApplicative<X>
