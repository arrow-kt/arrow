package arrow.core.extensions.tuple2.hash

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Hash
import arrow.typeclasses.Hash
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "hash(HA, HB)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Tuple2<A, B>.hash(HA: Hash<A>, HB: Hash<B>): Int = arrow.core.Tuple2.hash<A,
    B>(HA, HB).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B> Companion.hash(HA: Hash<A>, HB: Hash<B>): Tuple2Hash<A, B> = object :
    arrow.core.extensions.Tuple2Hash<A, B> { override fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB }
