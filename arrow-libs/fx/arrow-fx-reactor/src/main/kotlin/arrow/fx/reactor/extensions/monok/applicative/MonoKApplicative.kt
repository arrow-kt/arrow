package arrow.fx.reactor.extensions.monok.applicative

import arrow.Kind
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoK.Companion
import arrow.fx.reactor.extensions.MonoKApplicative
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
internal val applicative_singleton: MonoKApplicative = object :
  arrow.fx.reactor.extensions.MonoKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> A.just(): MonoK<A> = arrow.fx.reactor.MonoK.applicative().run {
  this@just.just<A>() as arrow.fx.reactor.MonoK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun unit(): MonoK<Unit> = arrow.fx.reactor.MonoK
  .applicative()
  .unit() as arrow.fx.reactor.MonoK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A, B> Kind<ForMonoK, A>.map(arg1: Function1<A, B>): MonoK<B> =
  arrow.fx.reactor.MonoK.applicative().run {
    this@map.map<A, B>(arg1) as arrow.fx.reactor.MonoK<B>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.replicate(arg1: Int): MonoK<List<A>> =
  arrow.fx.reactor.MonoK.applicative().run {
    this@replicate.replicate<A>(arg1) as arrow.fx.reactor.MonoK<kotlin.collections.List<A>>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateReactor)
fun <A> Kind<ForMonoK, A>.replicate(arg1: Int, arg2: Monoid<A>): MonoK<A> =
  arrow.fx.reactor.MonoK.applicative().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.fx.reactor.MonoK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateReactor)
inline fun Companion.applicative(): MonoKApplicative = applicative_singleton
