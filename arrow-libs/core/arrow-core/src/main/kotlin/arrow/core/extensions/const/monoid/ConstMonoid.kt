package arrow.core.extensions.const.monoid

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstMonoid
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
fun <A, T> Collection<Kind<Kind<ForConst, A>, T>>.combineAll(MA: Monoid<A>): Const<A, T> =
    arrow.core.Const.monoid<A, T>(MA).run {
  this@combineAll.combineAll() as arrow.core.Const<A, T>
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
  "arrow.core.Const.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> combineAll(MA: Monoid<A>, arg0: List<Kind<Kind<ForConst, A>, T>>): Const<A, T> =
    arrow.core.Const
   .monoid<A, T>(MA)
   .combineAll(arg0) as arrow.core.Const<A, T>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, T> Companion.monoid(MA: Monoid<A>): ConstMonoid<A, T> = object :
    arrow.core.extensions.ConstMonoid<A, T> { override fun MA(): arrow.typeclasses.Monoid<A> = MA }
