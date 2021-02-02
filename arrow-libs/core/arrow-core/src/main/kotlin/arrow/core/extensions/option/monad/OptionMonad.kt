package arrow.core.extensions.option.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionMonad

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: OptionMonad = object : arrow.core.extensions.OptionMonad {}

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
    "flatMap(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.flatMap(arg1: Function1<A, Kind<ForOption, B>>): Option<B> =
    arrow.core.Option.monad().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.Option<B>
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
  "Option.tailRecM(arg0, arg1)",
  "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForOption, Either<A, B>>>): Option<B> =
    arrow.core.Option
   .monad()
   .tailRecM<A, B>(arg0, arg1) as arrow.core.Option<B>

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
    "map(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.map(arg1: Function1<A, B>): Option<B> =
    arrow.core.Option.monad().run {
  this@map.map<A, B>(arg1) as arrow.core.Option<B>
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
  "ap(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.ap(arg1: Kind<ForOption, Function1<A, B>>): Option<B> =
    arrow.core.Option.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.core.Option<B>
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
fun <A> Kind<ForOption, Kind<ForOption, A>>.flatten(): Option<A> = arrow.core.Option.monad().run {
  this@flatten.flatten<A>() as arrow.core.Option<A>
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
    "flatMap { arg1 }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.followedBy(arg1: Kind<ForOption, B>): Option<B> =
    arrow.core.Option.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Option<B>
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
    "flatMap { a -> arg1.map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.apTap(arg1: Kind<ForOption, B>): Option<A> =
    arrow.core.Option.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Option<A>
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
    "flatMap { arg1.value() }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.followedByEval(arg1: Eval<Kind<ForOption, B>>): Option<B> =
    arrow.core.Option.monad().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.Option<B>
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
    "flatMap { a -> arg1(a).map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.effectM(arg1: Function1<A, Kind<ForOption, B>>): Option<A> =
    arrow.core.Option.monad().run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.Option<A>
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
    "flatMap { a -> arg1(a).map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.flatTap(arg1: Function1<A, Kind<ForOption, B>>): Option<A> =
    arrow.core.Option.monad().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.Option<A>
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
    "flatMap { a -> arg1.map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.productL(arg1: Kind<ForOption, B>): Option<A> =
    arrow.core.Option.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.core.Option<A>
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
    "flatMap { a -> arg1.map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.forEffect(arg1: Kind<ForOption, B>): Option<A> =
    arrow.core.Option.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.core.Option<A>
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
    "flatMap { a -> arg1.value().map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.productLEval(arg1: Eval<Kind<ForOption, B>>): Option<A> =
    arrow.core.Option.monad().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.Option<A>
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
    "flatMap { a -> arg1.value().map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.forEffectEval(arg1: Eval<Kind<ForOption, B>>): Option<A> =
    arrow.core.Option.monad().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.Option<A>
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
fun <A, B> Kind<ForOption, A>.mproduct(arg1: Function1<A, Kind<ForOption, B>>): Option<Tuple2<A, B>> =
  arrow.core.Option.monad().run {
    this@mproduct.mproduct<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>
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
fun <B> Kind<ForOption, Boolean>.ifM(
  arg1: Function0<Kind<ForOption, B>>,
  arg2: Function0<Kind<ForOption, B>>
): Option<B> = arrow.core.Option.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.Option<B>
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
fun <A, B> Kind<ForOption, Either<A, B>>.selectM(arg1: Kind<ForOption, Function1<A, B>>): Option<B> =
  arrow.core.Option.monad().run {
    this@selectM.selectM<A, B>(arg1) as arrow.core.Option<B>
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
    "selectM(arg1)",
    "arrow.core.selectM"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, Either<A, B>>.select(arg1: Kind<ForOption, Function1<A, B>>): Option<B> =
    arrow.core.Option.monad().run {
  this@select.select<A, B>(arg1) as arrow.core.Option<B>
}

/**
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
@Deprecated(
  "Monad typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monad(): OptionMonad = monad_singleton
