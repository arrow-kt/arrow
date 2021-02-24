package arrow.core.extensions.list.eq

import arrow.core.ListK
import arrow.core.extensions.ListKEq
import arrow.core.extensions.listk.eq.eq
import arrow.core.k
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import kotlin.Boolean
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(EqDeprecation, ReplaceWith("this == arg1"))
fun <A> List<A>.eqv(EQ: Eq<A>, arg1: List<A>): Boolean =
  ListK.eq(EQ).run { this@eqv.k().eqv(arg1.k()) }

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(EqDeprecation, ReplaceWith("this != arg1"))
fun <A> List<A>.neqv(EQ: Eq<A>, arg1: List<A>): Boolean =
  ListK.eq(EQ).run { this@neqv.k().neqv(arg1.k()) }

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(EqDeprecation)
  inline fun <A> eq(EQ: Eq<A>): ListKEq<A> = object : arrow.core.extensions.ListKEq<A> {
    override
    fun EQ(): arrow.typeclasses.Eq<A> = EQ
  }
}
