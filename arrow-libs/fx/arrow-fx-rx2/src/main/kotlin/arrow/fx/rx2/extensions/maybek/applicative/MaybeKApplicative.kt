package arrow.fx.rx2.extensions.maybek.applicative

import arrow.Kind
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKApplicative
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
internal val applicative_singleton: MaybeKApplicative = object :
    arrow.fx.rx2.extensions.MaybeKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> A.just(): MaybeK<A> = arrow.fx.rx2.MaybeK.applicative().run {
  this@just.just<A>() as arrow.fx.rx2.MaybeK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun unit(): MaybeK<Unit> = arrow.fx.rx2.MaybeK
   .applicative()
   .unit() as arrow.fx.rx2.MaybeK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.map(arg1: Function1<A, B>): MaybeK<B> =
    arrow.fx.rx2.MaybeK.applicative().run {
  this@map.map<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, A>.replicate(arg1: Int): MaybeK<List<A>> =
    arrow.fx.rx2.MaybeK.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.fx.rx2.MaybeK<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, A>.replicate(arg1: Int, arg2: Monoid<A>): MaybeK<A> =
    arrow.fx.rx2.MaybeK.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.fx.rx2.MaybeK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.applicative(): MaybeKApplicative = applicative_singleton
