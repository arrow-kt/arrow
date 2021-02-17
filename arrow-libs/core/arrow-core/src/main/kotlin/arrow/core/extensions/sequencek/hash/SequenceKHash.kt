package arrow.core.extensions.sequencek.hash

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> SequenceK<A>.hash(HA: Hash<A>): Int = arrow.core.SequenceK.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <A> Companion.hash(HA: Hash<A>): SequenceKHash<A> = object :
  arrow.core.extensions.SequenceKHash<A> { override fun HA(): arrow.typeclasses.Hash<A> = HA }
