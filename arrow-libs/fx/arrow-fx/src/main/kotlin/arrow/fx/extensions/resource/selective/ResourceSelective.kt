package arrow.fx.extensions.resource.selective

import arrow.Kind
import arrow.core.Either
import arrow.fx.ForResource
import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceSelective
import arrow.fx.typeclasses.Bracket
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, Either<A, B>>.select(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Function1<A, B>>
): Resource<F, E, B> =
  arrow.fx.Resource.selective<F, E>(BR).run {
    this@select.select<A, B>(arg1) as arrow.fx.Resource<F, E, B>
  }

@JvmName("branch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B, C> Kind<Kind<Kind<ForResource, F>, E>, Either<A, B>>.branch(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Function1<A, C>>,
  arg2: Kind<Kind<Kind<ForResource, F>, E>, Function1<B, C>>
): Resource<F, E, C> = arrow.fx.Resource.selective<F, E>(BR).run {
  this@branch.branch<A, B, C>(arg1, arg2) as arrow.fx.Resource<F, E, C>
}

@JvmName("whenS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, Boolean>.whenS(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Function0<Unit>>
): Resource<F, E, Unit> =
  arrow.fx.Resource.selective<F, E>(BR).run {
    this@whenS.whenS<A>(arg1) as arrow.fx.Resource<F, E, kotlin.Unit>
  }

@JvmName("ifS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, Boolean>.ifS(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, A>,
  arg2: Kind<Kind<Kind<ForResource, F>, E>, A>
): Resource<F, E, A> = arrow.fx.Resource.selective<F, E>(BR).run {
  this@ifS.ifS<A>(arg1, arg2) as arrow.fx.Resource<F, E, A>
}

@JvmName("orS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, Boolean>.orS(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Boolean>
): Resource<F, E, Boolean> =
  arrow.fx.Resource.selective<F, E>(BR).run {
    this@orS.orS<A>(arg1) as arrow.fx.Resource<F, E, kotlin.Boolean>
  }

@JvmName("andS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, Boolean>.andS(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Boolean>
): Resource<F, E, Boolean> =
  arrow.fx.Resource.selective<F, E>(BR).run {
    this@andS.andS<A>(arg1) as arrow.fx.Resource<F, E, kotlin.Boolean>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E> Companion.selective(BR: Bracket<F, E>): ResourceSelective<F, E> = object :
  arrow.fx.extensions.ResourceSelective<F, E> {
  override fun BR(): arrow.fx.typeclasses.Bracket<F,
    E> = BR
}
