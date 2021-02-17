package arrow.core.extensions.listk.hash

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
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
fun <A> Kind<ForListK, A>.hash(HA: Hash<A>): Int = arrow.core.ListK.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@JvmName("hashWithSalt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> Kind<ForListK, A>.hashWithSalt(HA: Hash<A>, arg1: Int): Int =
  arrow.core.ListK.hash<A>(HA).run {
    this@hashWithSalt.hashWithSalt(arg1) as kotlin.Int
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <A> Companion.hash(HA: Hash<A>): ListKHash<A> = object :
  arrow.core.extensions.ListKHash<A> { override fun HA(): arrow.typeclasses.Hash<A> = HA }
