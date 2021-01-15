package arrow.core.extensions.id.semigroup

import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdSemigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "plus(SA, arg1)",
  "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.plus(SA: Semigroup<A>, arg1: Id<A>): Id<A> = arrow.core.Id.semigroup<A>(SA).run {
  this@plus.plus(arg1) as arrow.core.Id<A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "maybeCombine(SA, arg1)",
  "arrow.core.maybeCombine"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.maybeCombine(SA: Semigroup<A>, arg1: Id<A>): Id<A> =
    arrow.core.Id.semigroup<A>(SA).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Id<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.semigroup(SA: Semigroup<A>): IdSemigroup<A> = object :
    arrow.core.extensions.IdSemigroup<A> { override fun SA(): arrow.typeclasses.Semigroup<A> = SA }
