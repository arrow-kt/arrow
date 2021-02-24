package arrow.fx.rx2.extensions.observablek.functorFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableK.Companion
import arrow.fx.rx2.extensions.ObservableKFunctorFilter
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
internal val functorFilter_singleton: ObservableKFunctorFilter = object :
  arrow.fx.rx2.extensions.ObservableKFunctorFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.filterMap(arg1: Function1<A, Option<B>>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.functorFilter().run {
    this@filterMap.filterMap<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, Option<A>>.flattenOption(): ObservableK<A> =
  arrow.fx.rx2.ObservableK.functorFilter().run {
    this@flattenOption.flattenOption<A>() as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A> Kind<ForObservableK, A>.filter(arg1: Function1<A, Boolean>): ObservableK<A> =
  arrow.fx.rx2.ObservableK.functorFilter().run {
    this@filter.filter<A>(arg1) as arrow.fx.rx2.ObservableK<A>
  }

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForObservableK, A>.filterIsInstance(arg1: Class<B>): ObservableK<B> =
  arrow.fx.rx2.ObservableK.functorFilter().run {
    this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.fx.rx2.ObservableK<B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.functorFilter(): ObservableKFunctorFilter = functorFilter_singleton
