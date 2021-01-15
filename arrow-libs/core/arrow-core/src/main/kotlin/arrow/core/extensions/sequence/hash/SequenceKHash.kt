package arrow.core.extensions.sequence.hash

import arrow.core.extensions.SequenceKHash
import arrow.typeclasses.Hash
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "hash(HA)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.hash(HA: Hash<A>): Int =
    arrow.core.extensions.sequence.hash.Sequence.hash<A>(HA).run {
  arrow.core.SequenceK(this@hash).hash() as kotlin.Int
}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> hash(HA: Hash<A>): SequenceKHash<A> = object :
      arrow.core.extensions.SequenceKHash<A> { override fun HA(): arrow.typeclasses.Hash<A> = HA }}
