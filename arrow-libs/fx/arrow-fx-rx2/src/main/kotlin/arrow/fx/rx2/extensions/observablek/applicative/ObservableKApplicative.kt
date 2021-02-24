package arrow.fx.rx2.extensions.observablek.applicative

import arrow.Kind
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKApplicative
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
internal val applicative_singleton: ObservableKApplicative = object :
  arrow.fx.rx2.extensions.ObservableKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> A.just(): ObservableK<A> = arrow.fx.rx2.ObservableK.applicative().run {
  this@just.just<A>() as arrow.fx.rx2.ObservableK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun unit(): ObservableK<Unit> = arrow.fx.rx2.ObservableK
  .applicative()
  .unit() as arrow.fx.rx2.ObservableK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.map(arg1: Function1<A, B>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.applicative().run {
    this@map.map<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.replicate(arg1: Int): ObservableK<List<A>> =
  arrow.fx.rx2.ObservableK.applicative().run {
    this@replicate.replicate<A>(arg1) as arrow.fx.rx2.ObservableK<kotlin.collections.List<A>>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.replicate(arg1: Int, arg2: Monoid<A>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.applicative().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.fx.rx2.ObservableK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicative(): ObservableKApplicative = applicative_singleton
