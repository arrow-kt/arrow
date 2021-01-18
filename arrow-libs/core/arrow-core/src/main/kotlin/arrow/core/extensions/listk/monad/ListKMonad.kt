package arrow.core.extensions.listk.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonad
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
internal val monad_singleton: ListKMonad = object : arrow.core.extensions.ListKMonad {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap(arg1)"))
fun <A, B> Kind<ForListK, A>.flatMap(arg1: Function1<A, Kind<ForListK, B>>): ListK<B> =
    arrow.core.ListK.monad().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("tailRecMIterable(arg0) { arg1(it) }"))
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForListK, Either<A, B>>>): ListK<B> =
    arrow.core.ListK
   .monad()
   .tailRecM<A, B>(arg0, arg1) as arrow.core.ListK<B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> Kind<ForListK, A>.map(arg1: Function1<A, B>): ListK<B> = arrow.core.ListK.monad().run {
  this@map.map<A, B>(arg1) as arrow.core.ListK<B>
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
@Deprecated("@extension projected functions are deprecated", ReplaceWith("ap(arg1)", "arrow.core.ap"))
fun <A, B> Kind<ForListK, A>.ap(arg1: Kind<ForListK, Function1<A, B>>): ListK<B> =
    arrow.core.ListK.monad().run {
  this@ap.ap<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatten"))
fun <A> Kind<ForListK, Kind<ForListK, A>>.flatten(): ListK<A> = arrow.core.ListK.monad().run {
  this@flatten.flatten<A>() as arrow.core.ListK<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { arg1 }"))
fun <A, B> Kind<ForListK, A>.followedBy(arg1: Kind<ForListK, B>): ListK<B> =
    arrow.core.ListK.monad().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> Kind<ForListK, A>.apTap(arg1: Kind<ForListK, B>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { arg1.value() }"))
fun <A, B> Kind<ForListK, A>.followedByEval(arg1: Eval<Kind<ForListK, B>>): ListK<B> =
    arrow.core.ListK.monad().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { a } }"))
fun <A, B> Kind<ForListK, A>.effectM(arg1: Function1<A, Kind<ForListK, B>>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { a } }"))
fun <A, B> Kind<ForListK, A>.flatTap(arg1: Function1<A, Kind<ForListK, B>>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> Kind<ForListK, A>.productL(arg1: Kind<ForListK, B>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@productL.productL<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> Kind<ForListK, A>.forEffect(arg1: Kind<ForListK, B>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.value().map { a } }"))
fun <A, B> Kind<ForListK, A>.productLEval(arg1: Eval<Kind<ForListK, B>>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.value().map { a } }"))
fun <A, B> Kind<ForListK, A>.forEffectEval(arg1: Eval<Kind<ForListK, B>>): ListK<A> =
    arrow.core.ListK.monad().run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.ListK<A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { b -> Tuple2(a, b) } }"))
fun <A, B> Kind<ForListK, A>.mproduct(arg1: Function1<A, Kind<ForListK, B>>): ListK<Tuple2<A, B>> =
    arrow.core.ListK.monad().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("ifM(arg1, arg2)", "arrow.core.ifM"))
fun <B> Kind<ForListK, Boolean>.ifM(
  arg1: Function0<Kind<ForListK, B>>,
  arg2: Function0<Kind<ForListK, B>>
): ListK<B> = arrow.core.ListK.monad().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.ListK<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("selectM(arg1)", "arrow.core.selectM"))
fun <A, B> Kind<ForListK, Either<A, B>>.selectM(arg1: Kind<ForListK, Function1<A, B>>): ListK<B> =
    arrow.core.ListK.monad().run {
  this@selectM.selectM<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("select(arg1)", "arrow.core.select"))
fun <A, B> Kind<ForListK, Either<A, B>>.select(arg1: Kind<ForListK, Function1<A, B>>): ListK<B> =
    arrow.core.ListK.monad().run {
  this@select.select<A, B>(arg1) as arrow.core.ListK<B>
}

/**
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
@Deprecated("Monad typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.monad(): ListKMonad = monad_singleton
