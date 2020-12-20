package arrow.core.extensions.either.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.flatMap as _flatMap
import arrow.core.flatten as _flatten
import arrow.core.ap as _ap
import arrow.core.ifM as _ifM
import arrow.core.selectM as _selectM
import arrow.core.mproduct as _mproduct
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.Tuple2
import arrow.core.extensions.EitherMonad
import arrow.core.fix
import kotlin.Any
import kotlin.Boolean
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: EitherMonad<Any?> = object : EitherMonad<Any?> {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap(arg1)", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.flatMap(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>): Either<L, B> =
  fix()._flatMap{ arg1(it).fix() }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.tailRecM(arg0, arg1)", "arrow.core.tailRecM"))
fun <L, A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<Kind<ForEither, L>, Either<A, B>>>): Either<L, B> =
  Either.tailRecM(arg0, arg1)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.map(arg1: Function1<A, B>): Either<L, B> =
  fix().map(arg1)

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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ap(arg1)", "arrow.core.ap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.ap(arg1: Kind<Kind<ForEither, L>, Function1<A, B>>): Either<L, B> =
  _ap(arg1)

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatten()", "arrow.core.flatten"))
fun <L, A> Kind<Kind<ForEither, L>, Kind<Kind<ForEither, L>, A>>.flatten(): Either<L, A> =
  map { it.fix() }._flatten()

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { arg1 }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.followedBy(arg1: Kind<Kind<ForEither, L>, B>): Either<L, B> =
  _flatMap { arg1.fix() }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Either.mapN(this, fb) { left, _ -> left }", "arrow.core.mapN"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.apTap(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  Either.mapN(fix(), arg1.fix()) { left, _ -> left }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { arg1.value() }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.followedByEval(arg1: Eval<Kind<Kind<ForEither, L>, B>>): Either<L, B> =
  _flatMap { arg1.value().fix() }

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { a } }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.effectM(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>): Either<L, A> =
  _flatMap { a -> arg1(a).map { a } }

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { a } }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.flatTap(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>): Either<L, A> =
  _flatMap { a -> arg1(a).map { a } }

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.productL(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  _flatMap { a -> arg1.map { a } }

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.forEffect(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  _flatMap { a -> arg1.map { a } }

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.value().map { a } }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.productLEval(arg1: Eval<Kind<Kind<ForEither, L>, B>>): Either<L, A> =
  _flatMap { a -> arg1.value().map { a } }

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.value().map { a } }", "arrow.core.flatMap"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.forEffectEval(arg1: Eval<Kind<Kind<ForEither, L>, B>>): Either<L, A> =
  _flatMap { a -> arg1.value().map { a } }

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("mproduct { arg1(it) }", "arrow.core.mproduct"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.mproduct(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>): Either<L, Tuple2<A, B>> =
  fix()._mproduct { arg1(it).fix() }

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ifM({ arg1() }, { arg2() })", "arrow.core.ifM"))
fun <L, B> Kind<Kind<ForEither, L>, Boolean>.ifM(
  arg1: Function0<Kind<Kind<ForEither, L>, B>>,
  arg2: Function0<Kind<Kind<ForEither, L>, B>>
): Either<L, B> =
  fix()._ifM({ arg1().fix() }, { arg2().fix() })

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("selectM(arg1)", "arrow.core.selectM"))
fun <L, A, B> Kind<Kind<ForEither, L>, Either<A, B>>.selectM(
  arg1: Kind<Kind<ForEither, L>,
    Function1<A, B>>
): Either<L, B> =
  fix()._selectM(arg1.fix())

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("selectM(arg1)", "arrow.core.selectM"))
fun <L, A, B> Kind<Kind<ForEither, L>, Either<A, B>>.select(
  arg1: Kind<Kind<ForEither, L>,
    Function1<A, B>>
): Either<L, B> =
  fix()._selectM(arg1.fix())

/**
 *  ank_macro_hierarchy(arrow.typeclasses.Monad)
 *
 *  [Monad] abstract over the ability to declare sequential computations that are dependent in the order or
 *  the results of previous computations.
 *
 *  Given a type constructor [F] with a value of [A] we can compose multiple operations of type
 *  `Kind<F, ?>` where `?` denotes a value being transformed.
 *
 *  This is true for all type constructors that can support the [Monad] type class including and not limited to
 *  [IO], [ObservableK], [Option], [Either], [List] ...
 *
 *  [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Monad typeclasses is deprecated. Use concrete methods on Either")
inline fun <L> Companion.monad(): EitherMonad<L> = monad_singleton as
  arrow.core.extensions.EitherMonad<L>
