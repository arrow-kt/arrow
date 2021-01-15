package arrow.core.extensions.id.monoid

import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.extensions.IdMonoid
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(MA)",
  "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<Id<A>>.combineAll(MA: Monoid<A>): Id<A> = arrow.core.Id.monoid<A>(MA).run {
  this@combineAll.combineAll() as arrow.core.Id<A>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(MA, arg0)",
  "arrow.core.Id.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(MA: Monoid<A>, arg0: List<Id<A>>): Id<A> = arrow.core.Id
   .monoid<A>(MA)
   .combineAll(arg0) as arrow.core.Id<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.monoid(MA: Monoid<A>): IdMonoid<A> = object :
    arrow.core.extensions.IdMonoid<A> { override fun MA(): arrow.typeclasses.Monoid<A> = MA }
