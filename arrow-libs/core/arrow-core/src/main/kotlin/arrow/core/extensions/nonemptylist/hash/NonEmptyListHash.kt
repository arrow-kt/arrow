package arrow.core.extensions.nonemptylist.hash

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> NonEmptyList<A>.hash(HA: Hash<A>): Int = arrow.core.NonEmptyList.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <A> Companion.hash(HA: Hash<A>): NonEmptyListHash<A> = object :
    arrow.core.extensions.NonEmptyListHash<A> { override fun HA(): arrow.typeclasses.Hash<A> = HA }
