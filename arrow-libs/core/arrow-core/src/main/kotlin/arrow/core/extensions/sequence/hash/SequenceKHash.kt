package arrow.core.extensions.sequence.hash

import arrow.core.extensions.SequenceKHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> Sequence<A>.hash(HA: Hash<A>): Int =
  arrow.core.extensions.sequence.hash.Sequence.hash<A>(HA).run {
    arrow.core.SequenceK(this@hash).hash() as kotlin.Int
  }

@Deprecated("Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions")
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
  inline fun <A> hash(HA: Hash<A>): SequenceKHash<A> = object :
    arrow.core.extensions.SequenceKHash<A> { override fun HA(): arrow.typeclasses.Hash<A> = HA }
}
