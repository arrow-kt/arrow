package arrow.core.extensions.nonemptylist.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple2
import arrow.core.extensions.NonEmptyListMonad
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
internal val monad_singleton: NonEmptyListMonad = object : arrow.core.extensions.NonEmptyListMonad
    {}

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
fun <A, B> Kind<ForNonEmptyList, A>.flatMap(arg1: Function1<A, Kind<ForNonEmptyList, B>>):
    NonEmptyList<B> = arrow.core.NonEmptyList.monad().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
  "arrow.core.NonEmptyList.tailRecM"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForNonEmptyList, Either<A, B>>>):
    NonEmptyList<B> = arrow.core.NonEmptyList
   .monad()
   .tailRecM<A, B>(arg0, arg1) as arrow.core.NonEmptyList<B>

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
fun <A, B> Kind<ForNonEmptyList, A>.map(arg1: Function1<A, B>): NonEmptyList<B> =
    arrow.core.NonEmptyList.monad().run {
  this@map.map<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, A>.ap(arg1: Kind<ForNonEmptyList, Function1<A, B>>):
    NonEmptyList<B> = arrow.core.NonEmptyList.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A> Kind<ForNonEmptyList, Kind<ForNonEmptyList, A>>.flatten(): NonEmptyList<A> =
    arrow.core.NonEmptyList.monad().run {
  this@flatten.flatten<A>() as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.followedBy(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<B> =
    arrow.core.NonEmptyList.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, A>.apTap(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<A> =
    arrow.core.NonEmptyList.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.followedByEval(arg1: Eval<Kind<ForNonEmptyList, B>>):
    NonEmptyList<B> = arrow.core.NonEmptyList.monad().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, A>.effectM(arg1: Function1<A, Kind<ForNonEmptyList, B>>):
    NonEmptyList<A> = arrow.core.NonEmptyList.monad().run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.flatTap(arg1: Function1<A, Kind<ForNonEmptyList, B>>):
    NonEmptyList<A> = arrow.core.NonEmptyList.monad().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.productL(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<A> =
    arrow.core.NonEmptyList.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.forEffect(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<A> =
    arrow.core.NonEmptyList.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.productLEval(arg1: Eval<Kind<ForNonEmptyList, B>>):
    NonEmptyList<A> = arrow.core.NonEmptyList.monad().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.forEffectEval(arg1: Eval<Kind<ForNonEmptyList, B>>):
    NonEmptyList<A> = arrow.core.NonEmptyList.monad().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.NonEmptyList<A>
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
fun <A, B> Kind<ForNonEmptyList, A>.mproduct(arg1: Function1<A, Kind<ForNonEmptyList, B>>):
    NonEmptyList<Tuple2<A, B>> = arrow.core.NonEmptyList.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>
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
fun <B> Kind<ForNonEmptyList, Boolean>.ifM(
  arg1: Function0<Kind<ForNonEmptyList, B>>,
  arg2: Function0<Kind<ForNonEmptyList, B>>
): NonEmptyList<B> =
    arrow.core.NonEmptyList.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, Either<A, B>>.selectM(
  arg1: Kind<ForNonEmptyList, Function1<A, B>>
): NonEmptyList<B> = arrow.core.NonEmptyList.monad().run {
  this@selectM.selectM<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, Either<A, B>>.select(arg1: Kind<ForNonEmptyList, Function1<A, B>>):
    NonEmptyList<B> = arrow.core.NonEmptyList.monad().run {
  this@select.select<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
inline fun Companion.monad(): NonEmptyListMonad = monad_singleton
