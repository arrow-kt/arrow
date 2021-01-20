package arrow.core.extensions.nonemptylist.semialign

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.NonEmptyListSemialign
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: NonEmptyListSemialign = object :
    arrow.core.extensions.NonEmptyListSemialign {}

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
  "arg0.fix().align(arg1.fix())",
  "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> align(arg0: Kind<ForNonEmptyList, A>, arg1: Kind<ForNonEmptyList, B>):
    NonEmptyList<Ior<A, B>> = arrow.core.NonEmptyList
   .semialign()
   .align<A, B>(arg0, arg1) as arrow.core.NonEmptyList<arrow.core.Ior<A, B>>

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
    "arg0.fix().align(arg1.fix()).map(arg2)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> alignWith(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Ior<A, B>, C>
): NonEmptyList<C> = arrow.core.NonEmptyList
   .semialign()
   .alignWith<A, B, C>(arg0, arg1, arg2) as arrow.core.NonEmptyList<C>

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
  "fix().salign(arg1, arg2.fix())",
  "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForNonEmptyList, A>.salign(arg1: Semigroup<A>, arg2: Kind<ForNonEmptyList, A>):
    NonEmptyList<A> = arrow.core.NonEmptyList.semialign().run {
  this@salign.salign<A>(arg1, arg2) as arrow.core.NonEmptyList<A>
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
  "fix().padZip(arg1.fix())",
  "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.padZip(arg1: Kind<ForNonEmptyList, B>):
    NonEmptyList<Tuple2<Option<A>, Option<B>>> = arrow.core.NonEmptyList.semialign().run {
  this@padZip.padZip<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<arrow.core.Option<A>,
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
    "fix().padZip(arg1.fix()).map(arg2)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForNonEmptyList, A>.padZipWith(
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function2<Option<A>, Option<B>, C>
): NonEmptyList<C> =
  arrow.core.NonEmptyList.semialign().run {
  this@padZipWith.padZipWith<A, B, C>(arg1, arg2) as arrow.core.NonEmptyList<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Semialign typeclass is deprecated. Use concrete methods on NonEmptyList")
inline fun Companion.semialign(): NonEmptyListSemialign = semialign_singleton
