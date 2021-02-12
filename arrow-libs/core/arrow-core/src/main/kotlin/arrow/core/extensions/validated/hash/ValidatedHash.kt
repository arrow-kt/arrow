package arrow.core.extensions.validated.hash

import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedHash
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
fun <L, R> Validated<L, R>.hash(HL: Hash<L>, HR: Hash<R>): Int =
  Validated.hash(HL, HR).run {
    this@hash.hash()
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <L, R> Companion.hash(HL: Hash<L>, HR: Hash<R>): ValidatedHash<L, R> = object :
  arrow.core.extensions.ValidatedHash<L, R> {
  override fun HL(): arrow.typeclasses.Hash<L> = HL

  override fun HR(): arrow.typeclasses.Hash<R> = HR
}
