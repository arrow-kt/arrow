package arrow.core.extensions.andthen.monoid

import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.AndThenDeprecation
import arrow.core.extensions.AndThenMonoid
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <A, B> Collection<AndThen<A, B>>.combineAll(MB: Monoid<B>): AndThen<A, B> =
  arrow.core.AndThen.monoid<A, B>(MB).run {
    this@combineAll.combineAll() as arrow.core.AndThen<A, B>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <A, B> combineAll(MB: Monoid<B>, arg0: List<AndThen<A, B>>): AndThen<A, B> = arrow.core.AndThen
  .monoid<A, B>(MB)
  .combineAll(arg0) as arrow.core.AndThen<A, B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(AndThenDeprecation)
inline fun <A, B> Companion.monoid(MB: Monoid<B>): AndThenMonoid<A, B> = object :
  arrow.core.extensions.AndThenMonoid<A, B> {
  override fun MB(): arrow.typeclasses.Monoid<B> = MB
}
