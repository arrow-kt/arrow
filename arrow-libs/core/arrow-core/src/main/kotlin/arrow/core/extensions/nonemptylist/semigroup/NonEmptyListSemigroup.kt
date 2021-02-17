package arrow.core.extensions.nonemptylist.semigroup

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListSemigroup
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: NonEmptyListSemigroup<Any?> = object : NonEmptyListSemigroup<Any?>
{}

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
    "plus(arg1)",
    "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
operator fun <A> NonEmptyList<A>.plus(arg1: NonEmptyList<A>): NonEmptyList<A> =
  arrow.core.NonEmptyList.semigroup<A>().run {
    this@plus.plus(arg1) as arrow.core.NonEmptyList<A>
  }

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
    "(arg1?.let { this.plus(it) } ?: this)",
    "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.maybeCombine(arg1: NonEmptyList<A>): NonEmptyList<A> =
  arrow.core.NonEmptyList.semigroup<A>().run {
    this@maybeCombine.maybeCombine(arg1) as arrow.core.NonEmptyList<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Semigroup.nonEmptyList<A>()",
    "arrow.core.Semigroup",
    "arrow.core.nonEmptyList"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.semigroup(): NonEmptyListSemigroup<A> = semigroup_singleton as
  arrow.core.extensions.NonEmptyListSemigroup<A>
