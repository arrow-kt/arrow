package arrow.fx.rx2.extensions.maybek.monadFilter

import arrow.Kind
import arrow.core.Option
import arrow.fx.rx2.DeprecateRxJava
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeK.Companion
import arrow.fx.rx2.extensions.MaybeKMonadFilter
import arrow.typeclasses.MonadFilterSyntax
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadFilter_singleton: MaybeKMonadFilter = object :
    arrow.fx.rx2.extensions.MaybeKMonadFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <A, B> Kind<ForMaybeK, A>.filterMap(arg1: Function1<A, Option<B>>): MaybeK<B> =
    arrow.fx.rx2.MaybeK.monadFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.fx.rx2.MaybeK<B>
}

@JvmName("bindingFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(DeprecateRxJava)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForMaybeK>.() -> B): MaybeK<B> =
    arrow.fx.rx2.MaybeK
   .monadFilter()
   .bindingFilter<B>(arg0) as arrow.fx.rx2.MaybeK<B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(DeprecateRxJava)
inline fun Companion.monadFilter(): MaybeKMonadFilter = monadFilter_singleton
