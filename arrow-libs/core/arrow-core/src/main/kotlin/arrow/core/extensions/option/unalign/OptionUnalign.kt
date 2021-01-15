package arrow.core.extensions.option.unalign

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Ior
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionUnalign
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: OptionUnalign = object : arrow.core.extensions.OptionUnalign {}

@JvmName("unalign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unalign(arg0)",
  "arrow.core.Option.unalign"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> unalign(arg0: Kind<ForOption, Ior<A, B>>): Tuple2<Kind<ForOption, A>, Kind<ForOption, B>> =
  arrow.core.Option
    .unalign()
    .unalign<A, B>(arg0) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForOption, A>,
    arrow.Kind<arrow.core.ForOption, B>>

@JvmName("unalignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unalignWith(arg0, arg1)",
  "arrow.core.Option.unalignWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> unalignWith(arg0: Kind<ForOption, C>, arg1: Function1<C, Ior<A, B>>):
    Tuple2<Kind<ForOption, A>, Kind<ForOption, B>> = arrow.core.Option
   .unalign()
   .unalignWith<A, B, C>(arg0, arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForOption, A>,
    arrow.Kind<arrow.core.ForOption, B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.unalign(): OptionUnalign = unalign_singleton
