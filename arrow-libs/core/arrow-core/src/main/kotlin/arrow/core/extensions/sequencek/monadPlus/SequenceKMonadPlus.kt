package arrow.core.extensions.sequencek.monadPlus

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonadPlus
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadPlus_singleton: SequenceKMonadPlus = object :
    arrow.core.extensions.SequenceKMonadPlus {}

@JvmName("zeroM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "zeroM()",
  "arrow.core.SequenceK.zeroM"
  ),
  DeprecationLevel.WARNING
)
fun <A> zeroM(): SequenceK<A> = arrow.core.SequenceK
   .monadPlus()
   .zeroM<A>() as arrow.core.SequenceK<A>

@JvmName("plusM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "plusM(arg1)",
  "arrow.core.plusM"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.plusM(arg1: Kind<ForSequenceK, A>): SequenceK<A> =
    arrow.core.SequenceK.monadPlus().run {
  this@plusM.plusM<A>(arg1) as arrow.core.SequenceK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monadPlus(): SequenceKMonadPlus = monadPlus_singleton
