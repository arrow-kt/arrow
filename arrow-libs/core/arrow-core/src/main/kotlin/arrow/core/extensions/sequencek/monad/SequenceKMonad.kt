package arrow.core.extensions.sequencek.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonad

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: SequenceKMonad = object : arrow.core.extensions.SequenceKMonad {}

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
    "this.flatMap(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.flatMap(arg1: Function1<A, Kind<ForSequenceK, B>>): SequenceK<B> =
  arrow.core.SequenceK.monad().run {
    this@flatMap.flatMap<A, B>(arg1) as arrow.core.SequenceK<B>
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
    "SequenceK.tailRecM(arg0, arg1)",
    "arrow.core.SequenceK"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForSequenceK, Either<A, B>>>): SequenceK<B> =
  arrow.core.SequenceK
    .monad()
    .tailRecM<A, B>(arg0, arg1) as arrow.core.SequenceK<B>

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
    "this.map(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.map(arg1: Function1<A, B>): SequenceK<B> =
  arrow.core.SequenceK.monad().run {
    this@map.map<A, B>(arg1) as arrow.core.SequenceK<B>
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
    "this.ap(arg1)",
    "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.ap(arg1: Kind<ForSequenceK, Function1<A, B>>): SequenceK<B> =
  arrow.core.SequenceK.monad().run {
    this@ap.ap<A, B>(arg1) as arrow.core.SequenceK<B>
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
    "this.flatten()",
    "arrow.core.flatten"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, Kind<ForSequenceK, A>>.flatten(): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@flatten.flatten<A>() as arrow.core.SequenceK<A>
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
    "this.flatMap { arg1 }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.followedBy(arg1: Kind<ForSequenceK, B>): SequenceK<B> =
  arrow.core.SequenceK.monad().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.core.SequenceK<B>
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
    "this.flatMap { a -> arg1.map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.apTap(arg1: Kind<ForSequenceK, B>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { arg1.value() }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.followedByEval(arg1: Eval<Kind<ForSequenceK, B>>): SequenceK<B> =
  arrow.core.SequenceK.monad().run {
    this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.SequenceK<B>
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
    "this.flatMap { a -> arg1(a).map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.effectM(arg1: Function1<A, Kind<ForSequenceK, B>>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@effectM.effectM<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { a -> arg1(a).map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.flatTap(arg1: Function1<A, Kind<ForSequenceK, B>>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@flatTap.flatTap<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { a -> arg1.map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.productL(arg1: Kind<ForSequenceK, B>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@productL.productL<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { a -> arg1.map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.forEffect(arg1: Kind<ForSequenceK, B>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@forEffect.forEffect<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { a -> arg1.value().map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.productLEval(arg1: Eval<Kind<ForSequenceK, B>>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@productLEval.productLEval<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { a -> arg1.value().map { a } }"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.forEffectEval(arg1: Eval<Kind<ForSequenceK, B>>): SequenceK<A> =
  arrow.core.SequenceK.monad().run {
    this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.SequenceK<A>
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
    "this.flatMap { a -> arg1(a).map { b -> Tuple2(a, b) } }",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.mproduct(arg1: Function1<A, Kind<ForSequenceK, B>>):
  SequenceK<Tuple2<A, B>> = arrow.core.SequenceK.monad().run {
    this@mproduct.mproduct<A, B>(arg1) as arrow.core.SequenceK<arrow.core.Tuple2<A, B>>
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
    "this.ifM(arg1, arg2)",
    "arrow.core.ifM"
  ),
  DeprecationLevel.WARNING
)
fun <B> Kind<ForSequenceK, Boolean>.ifM(
  arg1: Function0<Kind<ForSequenceK, B>>,
  arg2: Function0<Kind<ForSequenceK, B>>
): SequenceK<B> = arrow.core.SequenceK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.SequenceK<B>
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
    "this.selectM(arg1)",
    "arrow.core.selectM"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, Either<A, B>>.selectM(arg1: Kind<ForSequenceK, Function1<A, B>>):
  SequenceK<B> = arrow.core.SequenceK.monad().run {
    this@selectM.selectM<A, B>(arg1) as arrow.core.SequenceK<B>
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
    "this.selectM(arg1)",
    "arrow.core.selectM"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, Either<A, B>>.select(arg1: Kind<ForSequenceK, Function1<A, B>>):
  SequenceK<B> = arrow.core.SequenceK.monad().run {
    this@select.select<A, B>(arg1) as arrow.core.SequenceK<B>
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
  "Monad typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.monad(): SequenceKMonad = monad_singleton
