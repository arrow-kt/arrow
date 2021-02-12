package arrow.fx.extensions.resource.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.ForResource
import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceFunctor
import arrow.fx.typeclasses.Bracket
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

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
): Resource<F, E, B> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@map.map<A, B>(arg1) as arrow.fx.Resource<F, E, B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.imap(
  BR: Bracket<F, E>,
  arg1: Function1<A, B>,
  arg2: Function1<B, A>
): Resource<F, E, B> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.Resource<F, E, B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> lift(BR: Bracket<F, E>, arg0: Function1<A, B>):
  Function1<Kind<Kind<Kind<ForResource, F>, E>, A>, Kind<Kind<Kind<ForResource, F>, E>, B>> =
  arrow.fx.Resource
    .functor<F, E>(BR)
    .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForResource, F>,
    E>, A>, arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForResource, F>, E>, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> Kind<Kind<Kind<ForResource, F>, E>, A>.void(BR: Bracket<F, E>): Resource<F, E, Unit> =
  arrow.fx.Resource.functor<F, E>(BR).run {
    this@void.void<A>() as arrow.fx.Resource<F, E, kotlin.Unit>
  }

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.fproduct(
  BR: Bracket<F, E>,
  arg1: Function1<A, B>
): Resource<F, E, Tuple2<A, B>> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.Resource<F, E, arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.mapConst(BR: Bracket<F, E>, arg1: B):
  Resource<F, E, B> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.Resource<F, E, B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> A.mapConst(BR: Bracket<F, E>, arg1: Kind<Kind<Kind<ForResource, F>, E>, B>):
  Resource<F, E, A> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.Resource<F, E, A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.tupleLeft(BR: Bracket<F, E>, arg1: B):
  Resource<F, E, Tuple2<B, A>> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.Resource<F, E, arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A, B> Kind<Kind<Kind<ForResource, F>, E>, A>.tupleRight(BR: Bracket<F, E>, arg1: B):
  Resource<F, E, Tuple2<A, B>> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.Resource<F, E, arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, B, A : B> Kind<Kind<Kind<ForResource, F>, E>, A>.widen(BR: Bracket<F, E>): Resource<F, E,
  B> = arrow.fx.Resource.functor<F, E>(BR).run {
  this@widen.widen<B, A>() as arrow.fx.Resource<F, E, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E> Companion.functor(BR: Bracket<F, E>): ResourceFunctor<F, E> = object :
  arrow.fx.extensions.ResourceFunctor<F, E> {
  override fun BR(): arrow.fx.typeclasses.Bracket<F,
    E> = BR
}
