package arrow.fx.rx2.extensions.maybek.functorFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: MaybeKFunctorFilter = object :
    arrow.fx.rx2.extensions.MaybeKFunctorFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.filterMap(arg1: Function1<A, Option<B>>): MaybeK<B> =
    arrow.fx.rx2.MaybeK.functorFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, Option<A>>.flattenOption(): MaybeK<A> =
    arrow.fx.rx2.MaybeK.functorFilter().run {
  this@flattenOption.flattenOption<A>() as arrow.fx.rx2.MaybeK<A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForMaybeK, A>.filter(arg1: Function1<A, Boolean>): MaybeK<A> =
    arrow.fx.rx2.MaybeK.functorFilter().run {
  this@filter.filter<A>(arg1) as arrow.fx.rx2.MaybeK<A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.filterIsInstance(arg1: Class<B>): MaybeK<B> =
    arrow.fx.rx2.MaybeK.functorFilter().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functorFilter(): MaybeKFunctorFilter = functorFilter_singleton
