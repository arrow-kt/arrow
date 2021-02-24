package arrow.core.extensions.sequence.semialign

import arrow.core.Ior
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKSemialign
import arrow.typeclasses.Semigroup
import kotlin.sequences.Sequence

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
fun <A, B> align(arg0: Sequence<A>, arg1: Sequence<B>): Sequence<Ior<A, B>> =
  arrow.core.extensions.sequence.semialign.Sequence
    .semialign()
    .align<A, B>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<arrow.core.Ior<A, B>>

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
  arg0: Sequence<A>,
  arg1: Sequence<B>,
  arg2: Function1<Ior<A, B>, C>
): Sequence<C> = arrow.core.extensions.sequence.semialign.Sequence
  .semialign()
  .alignWith<A, B, C>(arrow.core.SequenceK(arg0), arrow.core.SequenceK(arg1), arg2) as
  kotlin.sequences.Sequence<C>

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
fun <A> Sequence<A>.salign(arg1: Semigroup<A>, arg2: Sequence<A>): Sequence<A> =
  arrow.core.extensions.sequence.semialign.Sequence.semialign().run {
    arrow.core.SequenceK(this@salign).salign<A>(arg1, arrow.core.SequenceK(arg2)) as
      kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.padZip(arg1: Sequence<B>): Sequence<Tuple2<Option<A>, Option<B>>> =
  arrow.core.extensions.sequence.semialign.Sequence.semialign().run {
    arrow.core.SequenceK(this@padZip).padZip<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<arrow.core.Tuple2<arrow.core.Option<A>, arrow.core.Option<B>>>
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
fun <A, B, C> Sequence<A>.padZipWith(arg1: Sequence<B>, arg2: Function2<Option<A>, Option<B>, C>):
  Sequence<C> = arrow.core.extensions.sequence.semialign.Sequence.semialign().run {
    arrow.core.SequenceK(this@padZipWith).padZipWith<A, B, C>(arrow.core.SequenceK(arg1), arg2) as
      kotlin.sequences.Sequence<C>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: SequenceKSemialign = object :
  arrow.core.extensions.SequenceKSemialign {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Semialign typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun semialign(): SequenceKSemialign = semialign_singleton
}
