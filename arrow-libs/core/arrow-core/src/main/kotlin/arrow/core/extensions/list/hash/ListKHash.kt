package arrow.core.extensions.list.hash

import arrow.core.ListK
import arrow.core.extensions.ListKHash
import arrow.core.extensions.listk.hash.hash
import arrow.core.k
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import kotlin.Int
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> List<A>.hash(HA: Hash<A>): Int =
  ListK.hash(HA).run {
    this@hash.k().hash()
  }

@JvmName("hashWithSalt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> List<A>.hashWithSalt(HA: Hash<A>, arg1: Int): Int =
  ListK.hash(HA).run {
    this@hashWithSalt.k().hashWithSalt(arg1)
  }

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(HashDeprecation)
  inline fun <A> hash(HA: Hash<A>): ListKHash<A> = object : arrow.core.extensions.ListKHash<A> {
    override fun HA(): arrow.typeclasses.Hash<A> = HA
  }
}
