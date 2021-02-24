package arrow.core.extensions.either.hash

import arrow.core.Either.Companion
import arrow.core.extensions.EitherHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import kotlin.Suppress

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <L, R> Companion.hash(HL: Hash<L>, HR: Hash<R>): EitherHash<L, R> = object :
  arrow.core.extensions.EitherHash<L, R> {
  override fun HL(): arrow.typeclasses.Hash<L> = HL

  override fun HR(): arrow.typeclasses.Hash<R> = HR
}
