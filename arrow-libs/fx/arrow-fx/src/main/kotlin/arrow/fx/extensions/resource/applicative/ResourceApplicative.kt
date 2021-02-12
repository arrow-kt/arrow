package arrow.fx.extensions.resource.applicative

import arrow.Kind
import arrow.fx.ForResource
import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceApplicative
import arrow.fx.typeclasses.Bracket
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
@Deprecated(IODeprecation)
fun <F, E, A> A.just(BR: Bracket<F, E>): Resource<F, E, A> = arrow.fx.Resource.applicative<F,
  E>(BR).run {
  this@just.just<A>() as arrow.fx.Resource<F, E, A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E> unit(BR: Bracket<F, E>): Resource<F, E, Unit> = arrow.fx.Resource
  .applicative<F, E>(BR)
  .unit() as arrow.fx.Resource<F, E, kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.map(
  BR: Bracket<F, E>,
  arg1: Function1<A,
    B>
): Resource<F, E, B> = arrow.fx.Resource.applicative<F, E>(BR).run {
  this@map.map<A, B>(arg1) as arrow.fx.Resource<F, E, B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, A>.replicate(BR: Bracket<F, E>, arg1: Int):
  Resource<F, E, List<A>> = arrow.fx.Resource.applicative<F, E>(BR).run {
  this@replicate.replicate<A>(arg1) as arrow.fx.Resource<F, E, kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, A>.replicate(
  BR: Bracket<F, E>,
  arg1: Int,
  arg2: Monoid<A>
): Resource<F, E, A> = arrow.fx.Resource.applicative<F, E>(BR).run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.fx.Resource<F, E, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E> Companion.applicative(BR: Bracket<F, E>): ResourceApplicative<F, E> = object :
  arrow.fx.extensions.ResourceApplicative<F, E> {
  override fun BR():
    arrow.fx.typeclasses.Bracket<F, E> = BR
}
