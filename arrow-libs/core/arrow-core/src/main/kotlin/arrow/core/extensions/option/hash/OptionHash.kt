package arrow.core.extensions.option.hash

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionHash
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
  "hashCode()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option<A>.hash(HA: Hash<A>): Int = arrow.core.Option.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Hash typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.hash(HA: Hash<A>): OptionHash<A> = object :
    arrow.core.extensions.OptionHash<A> { override fun HA(): arrow.typeclasses.Hash<A> = HA }
