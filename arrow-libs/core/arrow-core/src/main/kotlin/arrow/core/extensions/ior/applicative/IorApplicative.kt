package arrow.core.extensions.ior.applicative

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.extensions.IorApplicative
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
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
  "just(SL)",
  "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> A.just(SL: Semigroup<L>): Ior<L, A> = arrow.core.Ior.applicative<L>(SL).run {
  this@just.just<A>() as arrow.core.Ior<L, A>
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
  "unit(SL)",
  "arrow.core.Ior.unit"
  ),
  DeprecationLevel.WARNING
)
fun <L> unit(SL: Semigroup<L>): Ior<L, Unit> = arrow.core.Ior
   .applicative<L>(SL)
   .unit() as arrow.core.Ior<L, kotlin.Unit>

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
  "map(SL, arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.map(SL: Semigroup<L>, arg1: Function1<A, B>): Ior<L, B> =
    arrow.core.Ior.applicative<L>(SL).run {
  this@map.map<A, B>(arg1) as arrow.core.Ior<L, B>
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
  "replicate(SL, arg1)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.replicate(SL: Semigroup<L>, arg1: Int): Ior<L, List<A>> =
    arrow.core.Ior.applicative<L>(SL).run {
  this@replicate.replicate<A>(arg1) as arrow.core.Ior<L, kotlin.collections.List<A>>
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
  "replicate(SL, arg1, arg2)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.replicate(
  SL: Semigroup<L>,
  arg1: Int,
  arg2: Monoid<A>
): Ior<L, A> = arrow.core.Ior.applicative<L>(SL).run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Ior<L, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.applicative(SL: Semigroup<L>): IorApplicative<L> = object :
    arrow.core.extensions.IorApplicative<L> { override fun SL(): arrow.typeclasses.Semigroup<L> = SL
    }
