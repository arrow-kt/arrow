package arrow.fx.extensions.io.applicative

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOApplicative
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
internal val applicative_singleton: IOApplicative = object : arrow.fx.extensions.IOApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> A.just(): IO<A> = arrow.fx.IO.applicative().run {
  this@just.just<A>() as arrow.fx.IO<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun unit(): IO<Unit> = arrow.fx.IO
  .applicative()
  .unit() as arrow.fx.IO<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.map(arg1: Function1<A, B>): IO<B> = arrow.fx.IO.applicative().run {
  this@map.map<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.replicate(arg1: Int): IO<List<A>> = arrow.fx.IO.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.fx.IO<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A> Kind<ForIO, A>.replicate(arg1: Int, arg2: Monoid<A>): IO<A> =
  arrow.fx.IO.applicative().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.fx.IO<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.applicative(): IOApplicative = applicative_singleton
