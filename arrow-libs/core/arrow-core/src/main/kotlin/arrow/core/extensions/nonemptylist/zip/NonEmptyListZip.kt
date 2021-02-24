package arrow.core.extensions.nonemptylist.zip

import arrow.Kind
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple2
import arrow.core.extensions.NonEmptyListZip
import kotlin.Deprecated
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: NonEmptyListZip = object : arrow.core.extensions.NonEmptyListZip {}

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "fix().zip<B>(arg1.fix())",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForNonEmptyList, A>.zip(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<Tuple2<A, B>> =
  arrow.core.NonEmptyList.zip().run {
    this@zip.zip<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>
  }

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "fix().zip<B>(arg1.fix(), arg2)",
    "arrow.core.fix"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForNonEmptyList, A>.zipWith(
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function2<A, B, C>
): NonEmptyList<C> = arrow.core.NonEmptyList.zip().run {
  this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.NonEmptyList<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Zip typeclass is deprecated. Use concrete methods on NonEmptyList")
inline fun Companion.zip(): NonEmptyListZip = zip_singleton
