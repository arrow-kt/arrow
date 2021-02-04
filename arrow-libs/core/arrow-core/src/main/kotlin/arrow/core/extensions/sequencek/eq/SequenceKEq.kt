package arrow.core.extensions.sequencek.eq

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
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
  EqDeprecation,
  ReplaceWith("this != arg1"),
  DeprecationLevel.WARNING
)
fun <A> SequenceK<A>.neqv(EQ: Eq<A>, arg1: SequenceK<A>): Boolean =
    arrow.core.SequenceK.eq<A>(EQ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(EqDeprecation)
inline fun <A> Companion.eq(EQ: Eq<A>): SequenceKEq<A> = object :
    arrow.core.extensions.SequenceKEq<A> { override fun EQ(): arrow.typeclasses.Eq<A> = EQ }
