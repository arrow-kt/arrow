package arrow.core.extensions.function1.applicative

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Applicative
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Deprecated
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
internal val applicative_singleton: Function1Applicative<Any?> = object : Function1Applicative<Any?>
    {}

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
fun <I, A> A.just(): Function1<I, A> = arrow.core.Function1.applicative<I>().run {
  this@just.just<A>() as arrow.core.Function1<I, A>
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
  "arrow.core.Function1.unit"
  ),
  DeprecationLevel.WARNING
)
fun <I> unit(): Function1<I, Unit> = arrow.core.Function1
   .applicative<I>()
   .unit() as arrow.core.Function1<I, kotlin.Unit>

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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.map(arg1: kotlin.Function1<A, B>): Function1<I, B> =
    arrow.core.Function1.applicative<I>().run {
  this@map.map<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A> Kind<Kind<ForFunction1, I>, A>.replicate(arg1: Int): Function1<I, List<A>> =
    arrow.core.Function1.applicative<I>().run {
  this@replicate.replicate<A>(arg1) as arrow.core.Function1<I, kotlin.collections.List<A>>
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
fun <I, A> Kind<Kind<ForFunction1, I>, A>.replicate(arg1: Int, arg2: Monoid<A>): Function1<I, A> =
    arrow.core.Function1.applicative<I>().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Function1<I, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <I> Companion.applicative(): Function1Applicative<I> = applicative_singleton as
    arrow.core.extensions.Function1Applicative<I>
