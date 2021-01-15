package arrow.core.extensions.const.applicative

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstApplicative
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "just(MA)",
  "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <A> A.just(MA: Monoid<A>): Const<A, A> = arrow.core.Const.applicative<A>(MA).run {
  this@just.just<A>() as arrow.core.Const<A, A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unit(MA)",
  "arrow.core.Const.unit"
  ),
  DeprecationLevel.WARNING
)
fun <A> unit(MA: Monoid<A>): Const<A, Unit> = arrow.core.Const
   .applicative<A>(MA)
   .unit() as arrow.core.Const<A, kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(MA, arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.map(MA: Monoid<A>, arg1: Function1<A, B>): Const<A, B> =
    arrow.core.Const.applicative<A>(MA).run {
  this@map.map<A, B>(arg1) as arrow.core.Const<A, B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "replicate(MA, arg1)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.replicate(MA: Monoid<A>, arg1: Int): Const<A, List<A>> =
    arrow.core.Const.applicative<A>(MA).run {
  this@replicate.replicate<A>(arg1) as arrow.core.Const<A, kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "replicate(MA, arg1, arg2)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.replicate(
  MA: Monoid<A>,
  arg1: Int,
  arg2: Monoid<A>
): Const<A, A> = arrow.core.Const.applicative<A>(MA).run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Const<A, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.applicative(MA: Monoid<A>): ConstApplicative<A> = object :
    arrow.core.extensions.ConstApplicative<A> { override fun MA(): arrow.typeclasses.Monoid<A> = MA
    }
