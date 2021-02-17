package arrow.core.extensions.sequence.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonad
import kotlin.sequences.Sequence

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
fun <A, B> Sequence<A>.flatMap(arg1: Function1<A, Kind<ForSequenceK, B>>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@flatMap).flatMap<A, B>(arg1) as kotlin.sequences.Sequence<B>
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
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForSequenceK, Either<A, B>>>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence
    .monad()
    .tailRecM<A, B>(arg0, arg1) as kotlin.sequences.Sequence<B>

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
fun <A, B> Sequence<A>.map(arg1: Function1<A, B>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@map).map<A, B>(arg1) as kotlin.sequences.Sequence<B>
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
fun <A, B> Sequence<A>.ap(arg1: Sequence<Function1<A, B>>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@ap).ap<A, B>(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<B>
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
fun <A> Sequence<Sequence<A>>.flatten(): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@flatten).flatten<A>() as kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.followedBy(arg1: Sequence<B>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@followedBy).followedBy<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<B>
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
fun <A, B> Sequence<A>.apTap(arg1: Sequence<B>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@apTap).apTap<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.followedByEval(arg1: Eval<Kind<ForSequenceK, B>>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@followedByEval).followedByEval<A, B>(arg1) as
      kotlin.sequences.Sequence<B>
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
fun <A, B> Sequence<A>.effectM(arg1: Function1<A, Kind<ForSequenceK, B>>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@effectM).effectM<A, B>(arg1) as kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.flatTap(arg1: Function1<A, Kind<ForSequenceK, B>>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@flatTap).flatTap<A, B>(arg1) as kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.productL(arg1: Sequence<B>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@productL).productL<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.forEffect(arg1: Sequence<B>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@forEffect).forEffect<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.productLEval(arg1: Eval<Kind<ForSequenceK, B>>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@productLEval).productLEval<A, B>(arg1) as kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.forEffectEval(arg1: Eval<Kind<ForSequenceK, B>>): Sequence<A> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@forEffectEval).forEffectEval<A, B>(arg1) as kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.mproduct(arg1: Function1<A, Kind<ForSequenceK, B>>): Sequence<Tuple2<A, B>> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@mproduct).mproduct<A, B>(arg1) as
      kotlin.sequences.Sequence<arrow.core.Tuple2<A, B>>
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
fun <B> Sequence<Boolean>.ifM(
  arg1: Function0<Kind<ForSequenceK, B>>,
  arg2: Function0<Kind<ForSequenceK, B>>
): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@ifM).ifM<B>(arg1, arg2) as kotlin.sequences.Sequence<B>
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
fun <A, B> Sequence<Either<A, B>>.selectM(arg1: Sequence<Function1<A, B>>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@selectM).selectM<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<B>
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
fun <A, B> Sequence<Either<A, B>>.select(arg1: Sequence<Function1<A, B>>): Sequence<B> =
  arrow.core.extensions.sequence.monad.Sequence.monad().run {
    arrow.core.SequenceK(this@select).select<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<B>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: SequenceKMonad = object : arrow.core.extensions.SequenceKMonad {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
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
  inline fun monad(): SequenceKMonad = monad_singleton
}
