package arrow.core.extensions.tuple2.semigroup

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.combine
import arrow.core.extensions.Tuple2Semigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Pair(a, b).combine(SA, SB, Pair(arg1.a, arg1.b)).let { (a, b) -> Tuple2(a, b) }",
    "arrow.core.combine",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Tuple2<A, B>.plus(
  SA: Semigroup<A>,
  SB: Semigroup<B>,
  arg1: Tuple2<A, B>
): Tuple2<A, B> =
  Pair(a, b).combine(SA, SB, Pair(arg1.a, arg1.b)).let { (a, b) -> Tuple2(a, b) }

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "  arg1?.let { arg1 ->\n" +
      "    Pair(a, b).combine(SA, SB, Pair(arg1.a, arg1.b)).let { (a, b) -> Tuple2(a, b) }\n" +
      "  } ?: this",
    "arrow.core.combine",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Tuple2<A, B>.maybeCombine(
  SA: Semigroup<A>,
  SB: Semigroup<B>,
  arg1: Tuple2<A, B>
): Tuple2<A, B> =
  arg1?.let { arg1 ->
    Pair(a, b).combine(SA, SB, Pair(arg1.a, arg1.b)).let { (a, b) -> Tuple2(a, b) }
  } ?: this

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Tuple2 is deprecated in favor of Kotlin's Pair. ReplaceWith Pair and use Pair instance of Show",
  ReplaceWith(
    "Semigroup.pair(SA, SB)",
    "arrow.core.Semigroup",
    "arrow.core.pair"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B> Companion.semigroup(SA: Semigroup<A>, SB: Semigroup<B>): Tuple2Semigroup<A, B> =
  object : arrow.core.extensions.Tuple2Semigroup<A, B> {
    override fun SA():
      arrow.typeclasses.Semigroup<A> = SA

    override fun SB(): arrow.typeclasses.Semigroup<B> = SB
  }
