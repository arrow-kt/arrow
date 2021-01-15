package arrow.core.extensions.andthen.monad

import arrow.Kind
import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForAndThen
import arrow.core.Tuple2
import arrow.core.extensions.AndThenMonad
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: AndThenMonad<Any?> = object : AndThenMonad<Any?> {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flatMap(arg1)",
  "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.flatMap(
  arg1: Function1<A, Kind<Kind<ForAndThen, X>, B>>
): AndThen<X, B> = arrow.core.AndThen.monad<X>().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tailRecM(arg0, arg1)",
  "arrow.core.AndThen.tailRecM"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<Kind<ForAndThen, X>, Either<A, B>>>):
    AndThen<X, B> = arrow.core.AndThen
   .monad<X>()
   .tailRecM<A, B>(arg0, arg1) as arrow.core.AndThen<X, B>

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
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.map(arg1: Function1<A, B>): AndThen<X, B> =
    arrow.core.AndThen.monad<X>().run {
  this@map.map<A, B>(arg1) as arrow.core.AndThen<X, B>
}

/**
 *  @see [Apply.ap]
 */
@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ap(arg1)",
  "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.ap(arg1: Kind<Kind<ForAndThen, X>, Function1<A, B>>):
    AndThen<X, B> = arrow.core.AndThen.monad<X>().run {
  this@ap.ap<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flatten()",
  "arrow.core.flatten"
  ),
  DeprecationLevel.WARNING
)
fun <X, A> Kind<Kind<ForAndThen, X>, Kind<Kind<ForAndThen, X>, A>>.flatten(): AndThen<X, A> =
    arrow.core.AndThen.monad<X>().run {
  this@flatten.flatten<A>() as arrow.core.AndThen<X, A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "followedBy(arg1)",
  "arrow.core.followedBy"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.followedBy(arg1: Kind<Kind<ForAndThen, X>, B>):
    AndThen<X, B> = arrow.core.AndThen.monad<X>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apTap(arg1)",
  "arrow.core.apTap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.apTap(arg1: Kind<Kind<ForAndThen, X>, B>): AndThen<X, A> =
  arrow.core.AndThen.monad<X>().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.AndThen<X, A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "followedByEval(arg1)",
  "arrow.core.followedByEval"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.followedByEval(arg1: Eval<Kind<Kind<ForAndThen, X>, B>>):
    AndThen<X, B> = arrow.core.AndThen.monad<X>().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "effectM(arg1)",
  "arrow.core.effectM"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.effectM(
  arg1: Function1<A, Kind<Kind<ForAndThen, X>, B>>
): AndThen<X, A> = arrow.core.AndThen.monad<X>().run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.AndThen<X, A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flatTap(arg1)",
  "arrow.core.flatTap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.flatTap(
  arg1: Function1<A, Kind<Kind<ForAndThen, X>, B>>
): AndThen<X, A> = arrow.core.AndThen.monad<X>().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.AndThen<X, A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "productL(arg1)",
  "arrow.core.productL"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.productL(arg1: Kind<Kind<ForAndThen, X>, B>): AndThen<X,
    A> = arrow.core.AndThen.monad<X>().run {
  this@productL.productL<A, B>(arg1) as arrow.core.AndThen<X, A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "forEffect(arg1)",
  "arrow.core.forEffect"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.forEffect(arg1: Kind<Kind<ForAndThen, X>, B>): AndThen<X,
    A> = arrow.core.AndThen.monad<X>().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.core.AndThen<X, A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "productLEval(arg1)",
  "arrow.core.productLEval"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.productLEval(arg1: Eval<Kind<Kind<ForAndThen, X>, B>>):
    AndThen<X, A> = arrow.core.AndThen.monad<X>().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.AndThen<X, A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "forEffectEval(arg1)",
  "arrow.core.forEffectEval"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.forEffectEval(arg1: Eval<Kind<Kind<ForAndThen, X>, B>>):
    AndThen<X, A> = arrow.core.AndThen.monad<X>().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.AndThen<X, A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mproduct(arg1)",
  "arrow.core.mproduct"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.mproduct(
  arg1: Function1<A, Kind<Kind<ForAndThen, X>, B>>
): AndThen<X, Tuple2<A, B>> = arrow.core.AndThen.monad<X>().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ifM(arg1, arg2)",
  "arrow.core.ifM"
  ),
  DeprecationLevel.WARNING
)
fun <X, B> Kind<Kind<ForAndThen, X>, Boolean>.ifM(
  arg1: Function0<Kind<Kind<ForAndThen, X>, B>>,
  arg2: Function0<Kind<Kind<ForAndThen, X>, B>>
): AndThen<X, B> =
    arrow.core.AndThen.monad<X>().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.AndThen<X, B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "selectM(arg1)",
  "arrow.core.selectM"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, Either<A, B>>.selectM(
  arg1: Kind<Kind<ForAndThen, X>, Function1<A, B>>
): AndThen<X, B> = arrow.core.AndThen.monad<X>().run {
  this@selectM.selectM<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "select(arg1)",
  "arrow.core.select"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, Either<A, B>>.select(
  arg1: Kind<Kind<ForAndThen, X>, Function1<A, B>>
): AndThen<X, B> = arrow.core.AndThen.monad<X>().run {
  this@select.select<A, B>(arg1) as arrow.core.AndThen<X, B>
}

/**
 *  ank_macro_hierarchy(arrow.typeclasses.Monad)
 *
 *  [Monad] abstract over the ability to declare sequential computations that are dependent in the order or
 *  the results of previous computations.
 *
 *  Given a type constructor [F] with a value of [A] we can compose multiple operations of type
 *  `Kind<F, ?>` where `?` denotes a value being transformed.
 *
 *  This is true for all type constructors that can support the [Monad] type class including and not limited to [Option], [Either], [List] ...
 *
 *  [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <X> Companion.monad(): AndThenMonad<X> = monad_singleton as
    arrow.core.extensions.AndThenMonad<X>
