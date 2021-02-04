package arrow.core.extensions.set.eq

import arrow.core.extensions.SetKEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Set
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
fun <A> Set<A>.neqv(EQ: Eq<A>, arg1: Set<A>): Boolean =
  arrow.core.extensions.set.eq.Set.eq<A>(EQ).run {
    arrow.core.SetK(this@neqv).neqv(arrow.core.SetK(arg1)) as kotlin.Boolean
  }

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    EqDeprecation,
    level = DeprecationLevel.WARNING
  )
  inline fun <A> eq(EQ: Eq<A>): SetKEq<A> = object : arrow.core.extensions.SetKEq<A> {
    override fun EQ(): arrow.typeclasses.Eq<A> =
      EQ
  }
}
