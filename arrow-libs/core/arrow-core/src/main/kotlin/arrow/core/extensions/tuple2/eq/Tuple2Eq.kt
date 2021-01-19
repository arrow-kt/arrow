package arrow.core.extensions.tuple2.eq

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Eq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "neqv(EQA, EQB, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Tuple2<A, B>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  arg1: Tuple2<A, B>
): Boolean = arrow.core.Tuple2.eq<A, B>(EQA, EQB).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Tuple2 is deprecated in favor of Kotlin's Pair. ReplaceWith Pair and use Pair instance of Eq",
  ReplaceWith(
    "Eq.pair(EQA, EQB)",
    "arrow.core.Eq",
    "arrow.core.pair"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B> Companion.eq(EQA: Eq<A>, EQB: Eq<B>): Tuple2Eq<A, B> = object :
    arrow.core.extensions.Tuple2Eq<A, B> { override fun EQA(): arrow.typeclasses.Eq<A> = EQA

  override fun EQB(): arrow.typeclasses.Eq<B> = EQB }
