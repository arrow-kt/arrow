package arrow.fx.extensions.resource.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.ForResource
import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceMonad
import arrow.fx.typeclasses.Bracket
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.flatMap(
  BR: Bracket<F, E>,
  arg1: Function1<A, Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, B> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.fx.Resource<F, E, B>
  }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> tailRecM(
  BR: Bracket<F, E>,
  arg0: A,
  arg1: Function1<A, Kind<Kind<Kind<ForResource, F>, E>, Either<A, B>>>
): Resource<F, E, B> = arrow.fx.Resource
  .monad<F, E>(BR)
  .tailRecM<A, B>(arg0, arg1) as arrow.fx.Resource<F, E, B>

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
): Resource<F, E, B> = arrow.fx.Resource.monad<F, E>(BR).run {
  this@map.map<A, B>(arg1) as arrow.fx.Resource<F, E, B>
}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.ap(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Function1<A, B>>
): Resource<F, E, B> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@ap.ap<A, B>(arg1) as arrow.fx.Resource<F, E, B>
  }

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, Kind<Kind<Kind<ForResource, F>, E>,
  A>>.flatten(BR: Bracket<F, E>): Resource<F, E, A> = arrow.fx.Resource.monad<F, E>(BR).run {
  this@flatten.flatten<A>() as arrow.fx.Resource<F, E, A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.followedBy(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, B>
): Resource<F, E, B> = arrow.fx.Resource.monad<F,
  E>(BR).run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.fx.Resource<F, E, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.apTap(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, B>
): Resource<F, E, A> = arrow.fx.Resource.monad<F,
  E>(BR).run {
  this@apTap.apTap<A, B>(arg1) as arrow.fx.Resource<F, E, A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.followedByEval(
  BR: Bracket<F, E>,
  arg1: Eval<Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, B> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.fx.Resource<F, E, B>
  }

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.effectM(
  BR: Bracket<F, E>,
  arg1: Function1<A, Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, A> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@effectM.effectM<A, B>(arg1) as arrow.fx.Resource<F, E, A>
  }

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.flatTap(
  BR: Bracket<F, E>,
  arg1: Function1<A, Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, A> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.fx.Resource<F, E, A>
  }

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.productL(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, B>
): Resource<F, E, A> = arrow.fx.Resource.monad<F,
  E>(BR).run {
  this@productL.productL<A, B>(arg1) as arrow.fx.Resource<F, E, A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.forEffect(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, B>
): Resource<F, E, A> = arrow.fx.Resource.monad<F,
  E>(BR).run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.fx.Resource<F, E, A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.productLEval(
  BR: Bracket<F, E>,
  arg1: Eval<Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, A> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.fx.Resource<F, E, A>
  }

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.forEffectEval(
  BR: Bracket<F, E>,
  arg1: Eval<Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, A> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.fx.Resource<F, E, A>
  }

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.mproduct(
  BR: Bracket<F, E>,
  arg1: Function1<A, Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, Tuple2<A, B>> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@mproduct.mproduct<A, B>(arg1) as arrow.fx.Resource<F, E, arrow.core.Tuple2<A, B>>
  }

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, B> Kind<Kind<Kind<ForResource, F>, E>, Boolean>.ifM(
  BR: Bracket<F, E>,
  arg1: Function0<Kind<Kind<Kind<ForResource, F>, E>, B>>,
  arg2: Function0<Kind<Kind<Kind<ForResource, F>, E>, B>>
): Resource<F, E, B> = arrow.fx.Resource.monad<F, E>(BR).run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.fx.Resource<F, E, B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, Either<A, B>>.selectM(
  BR: Bracket<F, E>,
  arg1: Kind<Kind<Kind<ForResource, F>, E>, Function1<A, B>>
): Resource<F, E, B> =
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@selectM.selectM<A, B>(arg1) as arrow.fx.Resource<F, E, B>
  }

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
  arrow.fx.Resource.monad<F, E>(BR).run {
    this@select.select<A, B>(arg1) as arrow.fx.Resource<F, E, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E> Companion.monad(BR: Bracket<F, E>): ResourceMonad<F, E> = object :
  arrow.fx.extensions.ResourceMonad<F, E> {
  override fun BR(): arrow.fx.typeclasses.Bracket<F, E> =
    BR
}
