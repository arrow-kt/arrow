package arrow.core.extensions.sequencek.semialign

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Ior
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKSemialign
import arrow.typeclasses.Semigroup

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: SequenceKSemialign = object :
  arrow.core.extensions.SequenceKSemialign {}

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
    "arg0.align(arg1)",
    "arrow.core.align"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> align(arg0: Kind<ForSequenceK, A>, arg1: Kind<ForSequenceK, B>): SequenceK<Ior<A, B>> =
  arrow.core.SequenceK
    .semialign()
    .align<A, B>(arg0, arg1) as arrow.core.SequenceK<arrow.core.Ior<A, B>>

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
    "arg0.align(arg1, arg2)",
    "arrow.core.align"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> alignWith(
  arg0: Kind<ForSequenceK, A>,
  arg1: Kind<ForSequenceK, B>,
  arg2: Function1<Ior<A, B>, C>
): SequenceK<C> = arrow.core.SequenceK
  .semialign()
  .alignWith<A, B, C>(arg0, arg1, arg2) as arrow.core.SequenceK<C>

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
    "this.align(arg1, arg2)",
    "arrow.core.align"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.salign(arg1: Semigroup<A>, arg2: Kind<ForSequenceK, A>): SequenceK<A> =
  arrow.core.SequenceK.semialign().run {
    this@salign.salign<A>(arg1, arg2) as arrow.core.SequenceK<A>
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
    "this.padZip(arg1)",
    "arrow.core.padZip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.padZip(arg1: Kind<ForSequenceK, B>): SequenceK<Tuple2<Option<A>,
    Option<B>>> = arrow.core.SequenceK.semialign().run {
  this@padZip.padZip<A, B>(arg1) as arrow.core.SequenceK<arrow.core.Tuple2<arrow.core.Option<A>,
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
    "this.padZip(arg1, arg2)",
    "arrow.core.padZip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForSequenceK, A>.padZipWith(
  arg1: Kind<ForSequenceK, B>,
  arg2: Function2<Option<A>, Option<B>, C>
): SequenceK<C> = arrow.core.SequenceK.semialign().run {
  this@padZipWith.padZipWith<A, B, C>(arg1, arg2) as arrow.core.SequenceK<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Semialign typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.semialign(): SequenceKSemialign = semialign_singleton
