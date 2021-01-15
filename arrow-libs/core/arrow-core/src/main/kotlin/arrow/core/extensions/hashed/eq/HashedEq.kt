package arrow.core.extensions.hashed.eq

import arrow.core.Hashed
import arrow.core.Hashed.Companion
import arrow.core.extensions.HashedEq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "neqv(EQA, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.neqv(EQA: Eq<A>, arg1: Hashed<A>): Boolean = arrow.core.Hashed.eq<A>(EQA).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.eq(EQA: Eq<A>): HashedEq<A> = object : arrow.core.extensions.HashedEq<A> {
    override fun EQA(): arrow.typeclasses.Eq<A> = EQA }
