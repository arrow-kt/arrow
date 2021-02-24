package arrow.core.extensions.option.semialign

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionSemialign
import arrow.typeclasses.Semigroup

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: OptionSemialign = object : arrow.core.extensions.OptionSemialign
{}

@JvmName("align")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg0.align(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> align(arg0: Kind<ForOption, A>, arg1: Kind<ForOption, B>): Option<Ior<A, B>> =
  arrow.core.Option
    .semialign()
    .align<A, B>(arg0, arg1) as arrow.core.Option<arrow.core.Ior<A, B>>

@JvmName("alignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg0.align(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> alignWith(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Function1<Ior<A, B>, C>
): Option<C> = arrow.core.Option
  .semialign()
  .alignWith<A, B, C>(arg0, arg1, arg2) as arrow.core.Option<C>

@JvmName("salign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "salign(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.salign(arg1: Semigroup<A>, arg2: Kind<ForOption, A>): Option<A> =
  arrow.core.Option.semialign().run {
    this@salign.salign<A>(arg1, arg2) as arrow.core.Option<A>
  }

@JvmName("padZip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "padZip(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.padZip(arg1: Kind<ForOption, B>): Option<Tuple2<Option<A>, Option<B>>> =
  arrow.core.Option.semialign().run {
    this@padZip.padZip<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<arrow.core.Option<A>,
        arrow.core.Option<B>>>
  }

@JvmName("padZipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "padZip(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForOption, A>.padZipWith(
  arg1: Kind<ForOption, B>,
  arg2: Function2<Option<A>, Option<B>, C>
): Option<C> = arrow.core.Option.semialign().run {
  this@padZipWith.padZipWith<A, B, C>(arg1, arg2) as arrow.core.Option<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Semialign typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.semialign(): OptionSemialign = semialign_singleton
