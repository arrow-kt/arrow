package arrow.core.extensions.tuple2.applicative

import arrow.Kind
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Applicative
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
  "just(MF)",
  "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> A.just(MF: Monoid<F>): Tuple2<F, A> = arrow.core.Tuple2.applicative<F>(MF).run {
  this@just.just<A>() as arrow.core.Tuple2<F, A>
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
  "unit(MF)",
  "arrow.core.Tuple2.unit"
  ),
  DeprecationLevel.WARNING
)
fun <F> unit(MF: Monoid<F>): Tuple2<F, Unit> = arrow.core.Tuple2
   .applicative<F>(MF)
   .unit() as arrow.core.Tuple2<F, kotlin.Unit>

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
  "map(MF, arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.map(MF: Monoid<F>, arg1: Function1<A, B>): Tuple2<F, B> =
    arrow.core.Tuple2.applicative<F>(MF).run {
  this@map.map<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
  "replicate(MF, arg1)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.replicate(MF: Monoid<F>, arg1: Int): Tuple2<F, List<A>> =
    arrow.core.Tuple2.applicative<F>(MF).run {
  this@replicate.replicate<A>(arg1) as arrow.core.Tuple2<F, kotlin.collections.List<A>>
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
  "replicate(MF, arg1, arg2)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.replicate(
  MF: Monoid<F>,
  arg1: Int,
  arg2: Monoid<A>
): Tuple2<F, A> = arrow.core.Tuple2.applicative<F>(MF).run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Tuple2<F, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <F> Companion.applicative(MF: Monoid<F>): Tuple2Applicative<F> = object :
    arrow.core.extensions.Tuple2Applicative<F> { override fun MF(): arrow.typeclasses.Monoid<F> = MF
    }
