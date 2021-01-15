package arrow.core.extensions.sequence.eq

import arrow.core.extensions.SequenceKEq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

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
  "neqv(EQ, arg1)",
  "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.neqv(EQ: Eq<A>, arg1: Sequence<A>): Boolean =
    arrow.core.extensions.sequence.eq.Sequence.eq<A>(EQ).run {
  arrow.core.SequenceK(this@neqv).neqv(arrow.core.SequenceK(arg1)) as kotlin.Boolean
}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> eq(EQ: Eq<A>): SequenceKEq<A> = object : arrow.core.extensions.SequenceKEq<A> {
      override fun EQ(): arrow.typeclasses.Eq<A> = EQ }}
