package arrow.fx.rx2.extensions.singlek.applicative

import arrow.Kind
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleK.Companion
import arrow.fx.rx2.extensions.SingleKApplicative
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
internal val applicative_singleton: SingleKApplicative = object :
    arrow.fx.rx2.extensions.SingleKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> A.just(): SingleK<A> = arrow.fx.rx2.SingleK.applicative().run {
  this@just.just<A>() as arrow.fx.rx2.SingleK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun unit(): SingleK<Unit> = arrow.fx.rx2.SingleK
   .applicative()
   .unit() as arrow.fx.rx2.SingleK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForSingleK, A>.map(arg1: Function1<A, B>): SingleK<B> =
    arrow.fx.rx2.SingleK.applicative().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.SingleK<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.replicate(arg1: Int): SingleK<List<A>> =
    arrow.fx.rx2.SingleK.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.fx.rx2.SingleK<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForSingleK, A>.replicate(arg1: Int, arg2: Monoid<A>): SingleK<A> =
    arrow.fx.rx2.SingleK.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.fx.rx2.SingleK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicative(): SingleKApplicative = applicative_singleton
