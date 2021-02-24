package arrow.core.extensions.eval.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Eval.Companion
import arrow.core.ForEval
import arrow.core.Tuple2
import arrow.core.extensions.EvalMonad

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: EvalMonad = object : arrow.core.extensions.EvalMonad {}

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
fun <A, B> Kind<ForEval, A>.flatMap(arg1: Function1<A, Kind<ForEval, B>>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.core.Eval<B>
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
    "Eval.tailRecM(arg0, arg1)",
    "arrow.core.Eval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForEval, Either<A, B>>>): Eval<B> =
  arrow.core.Eval
    .monad()
    .tailRecM<A, B>(arg0, arg1) as arrow.core.Eval<B>

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
fun <A, B> Kind<ForEval, A>.map(arg1: Function1<A, B>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@map.map<A, B>(arg1) as arrow.core.Eval<B>
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
fun <A, B> Kind<ForEval, A>.ap(arg1: Kind<ForEval, Function1<A, B>>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@ap.ap<A, B>(arg1) as arrow.core.Eval<B>
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
    "flatMap(::identity)",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForEval, Kind<ForEval, A>>.flatten(): Eval<A> =
  arrow.core.Eval.monad().run {
    this@flatten.flatten<A>() as arrow.core.Eval<A>
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
    "flatMap { arg1 }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.followedBy(arg1: Kind<ForEval, B>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.core.Eval<B>
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
fun <A, B> Kind<ForEval, A>.apTap(arg1: Kind<ForEval, B>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.Eval<A>
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
    "flatMap { arg1.value() }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.followedByEval(arg1: Eval<Kind<ForEval, B>>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.Eval<B>
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
    "flatTap(arg1)",
    "arrow.core.flatTap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.effectM(arg1: Function1<A, Kind<ForEval, B>>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@effectM.effectM<A, B>(arg1) as arrow.core.Eval<A>
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
fun <A, B> Kind<ForEval, A>.flatTap(arg1: Function1<A, Kind<ForEval, B>>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.core.Eval<A>
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
    "flatMap { a -> arg1.map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.productL(arg1: Kind<ForEval, B>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@productL.productL<A, B>(arg1) as arrow.core.Eval<A>
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
    "flatMap { a -> arg1.map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.forEffect(arg1: Kind<ForEval, B>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@forEffect.forEffect<A, B>(arg1) as arrow.core.Eval<A>
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
    "flatMap { a -> arg1.value().map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.productLEval(arg1: Eval<Kind<ForEval, B>>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.core.Eval<A>
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
    "flatMap { a -> arg1.value().map { a } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.forEffectEval(arg1: Eval<Kind<ForEval, B>>): Eval<A> =
  arrow.core.Eval.monad().run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.Eval<A>
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
    "flatMap { a -> arg1(a).map { Tuple2(a, it) } }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, A>.mproduct(arg1: Function1<A, Kind<ForEval, B>>): Eval<Tuple2<A, B>> =
  arrow.core.Eval.monad().run {
    this@mproduct.mproduct<A, B>(arg1) as arrow.core.Eval<arrow.core.Tuple2<A, B>>
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
    "flatMap { if (it) arg1() else arg2() }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <B> Kind<ForEval, Boolean>.ifM(
  arg1: Function0<Kind<ForEval, B>>,
  arg2: Function0<Kind<ForEval, B>>
): Eval<B> = arrow.core.Eval.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.Eval<B>
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
    "flatMap { it.fold({ a -> arg1.map { ff -> ff(a) } }, { b -> just(b) }) }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, Either<A, B>>.selectM(arg1: Kind<ForEval, Function1<A, B>>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@selectM.selectM<A, B>(arg1) as arrow.core.Eval<B>
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
    "flatMap { it.fold({ a -> arg1.map { ff -> ff(a) } }, { b -> just(b) }) }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForEval, Either<A, B>>.select(arg1: Kind<ForEval, Function1<A, B>>): Eval<B> =
  arrow.core.Eval.monad().run {
    this@select.select<A, B>(arg1) as arrow.core.Eval<B>
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
@Deprecated("Monad typeclass is deprecated. Use concrete methods on Eval")
inline fun Companion.monad(): EvalMonad = monad_singleton
