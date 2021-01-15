package arrow.core.extensions.function1.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.Tuple2
import arrow.core.extensions.Function1Monad
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
internal val monad_singleton: Function1Monad<Any?> = object : Function1Monad<Any?> {}

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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.flatMap(
  arg1: Function1<A, Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, B> = arrow.core.Function1.monad<I>().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.Function1<I, B>
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
  "arrow.core.Function1.tailRecM"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<Kind<ForFunction1, I>, Either<A, B>>>):
    arrow.core.Function1<I, B> = arrow.core.Function1
   .monad<I>()
   .tailRecM<A, B>(arg0, arg1) as arrow.core.Function1<I, B>

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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.map(arg1: Function1<A, B>): arrow.core.Function1<I, B> =
  arrow.core.Function1.monad<I>().run {
    this@map.map<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.ap(arg1: Kind<Kind<ForFunction1, I>, Function1<A, B>>):
    arrow.core.Function1<I, B> = arrow.core.Function1.monad<I>().run {
  this@ap.ap<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A> Kind<Kind<ForFunction1, I>, Kind<Kind<ForFunction1, I>, A>>.flatten():
    arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@flatten.flatten<A>() as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.followedBy(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, B> = arrow.core.Function1.monad<I>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.apTap(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.followedByEval(
  arg1: Eval<Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, B> = arrow.core.Function1.monad<I>().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.effectM(
  arg1: Function1<A, Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.flatTap(
  arg1: Function1<A, Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.productL(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@productL.productL<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.forEffect(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.productLEval(
  arg1: Eval<Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.forEffectEval(
  arg1: Eval<Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, A> = arrow.core.Function1.monad<I>().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.Function1<I, A>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.mproduct(
  arg1: Function1<A, Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, Tuple2<A, B>> = arrow.core.Function1.monad<I>().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.Function1<I, arrow.core.Tuple2<A, B>>
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
fun <I, B> Kind<Kind<ForFunction1, I>, Boolean>.ifM(
  arg1: Function0<Kind<Kind<ForFunction1, I>, B>>,
  arg2: Function0<Kind<Kind<ForFunction1, I>, B>>
): arrow.core.Function1<I, B> =
  arrow.core.Function1.monad<I>().run {
    this@ifM.ifM<B>(arg1, arg2) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, Either<A, B>>.selectM(
  arg1: Kind<Kind<ForFunction1, I>, Function1<A, B>>
): arrow.core.Function1<I, B> = arrow.core.Function1.monad<I>().run {
  this@selectM.selectM<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, Either<A, B>>.select(
  arg1: Kind<Kind<ForFunction1, I>, Function1<A, B>>
): arrow.core.Function1<I, B> = arrow.core.Function1.monad<I>().run {
  this@select.select<A, B>(arg1) as arrow.core.Function1<I, B>
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
inline fun <I> Companion.monad(): Function1Monad<I> = monad_singleton as
    arrow.core.extensions.Function1Monad<I>
