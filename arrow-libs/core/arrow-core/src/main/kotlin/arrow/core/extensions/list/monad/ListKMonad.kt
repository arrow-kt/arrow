package arrow.core.extensions.list.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonad
import arrow.core.fix
import arrow.core.tailRecMIterable
import kotlin.Boolean
import kotlin.collections.flatMap as _flatMap
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap(arg1)"))
fun <A, B> List<A>.flatMap(arg1: Function1<A, Kind<ForListK, B>>): List<B> =
  _flatMap { arg1(it).fix() }

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("tailRecMIterable(arg0) { arg1(it) }"))
fun <A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<ForListK, Either<A, B>>>): List<B> =
  tailRecMIterable(arg0) { arg1(it).fix() }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> List<A>.map(arg1: Function1<A, B>): List<B> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@map).map<A, B>(arg1) as kotlin.collections.List<B>
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
fun <A, B> List<A>.ap(arg1: List<Function1<A, B>>): List<B> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@ap).ap<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatten"))
fun <A> List<List<A>>.flatten(): List<A> = arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@flatten).flatten<A>() as kotlin.collections.List<A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { arg1 }"))
fun <A, B> List<A>.followedBy(arg1: List<B>): List<B> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@followedBy).followedBy<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> List<A>.apTap(arg1: List<B>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@apTap).apTap<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { arg1.value() }"))
fun <A, B> List<A>.followedByEval(arg1: Eval<Kind<ForListK, B>>): List<B> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@followedByEval).followedByEval<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { a } }"))
fun <A, B> List<A>.effectM(arg1: Function1<A, Kind<ForListK, B>>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@effectM).effectM<A, B>(arg1) as kotlin.collections.List<A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { a } }"))
fun <A, B> List<A>.flatTap(arg1: Function1<A, Kind<ForListK, B>>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@flatTap).flatTap<A, B>(arg1) as kotlin.collections.List<A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> List<A>.productL(arg1: List<B>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@productL).productL<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> List<A>.forEffect(arg1: List<B>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@forEffect).forEffect<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.value().map { a } }"))
fun <A, B> List<A>.productLEval(arg1: Eval<Kind<ForListK, B>>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@productLEval).productLEval<A, B>(arg1) as kotlin.collections.List<A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.value().map { a } }"))
fun <A, B> List<A>.forEffectEval(arg1: Eval<Kind<ForListK, B>>): List<A> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@forEffectEval).forEffectEval<A, B>(arg1) as kotlin.collections.List<A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("flatMap { a -> arg1(a).map { b -> Tuple2(a, b) } }"))
fun <A, B> List<A>.mproduct(arg1: Function1<A, Kind<ForListK, B>>): List<Tuple2<A, B>> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@mproduct).mproduct<A, B>(arg1) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("ifM(arg1, arg2)", "arrow.core.ifM"))
fun <B> List<Boolean>.ifM(arg1: Function0<Kind<ForListK, B>>, arg2: Function0<Kind<ForListK, B>>):
    List<B> = arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@ifM).ifM<B>(arg1, arg2) as kotlin.collections.List<B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("selectM(arg1)", "arrow.core.selectM"))
fun <A, B> List<Either<A, B>>.selectM(arg1: List<Function1<A, B>>): List<B> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@selectM).selectM<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("select(arg1)", "arrow.core.select"))
fun <A, B> List<Either<A, B>>.select(arg1: List<Function1<A, B>>): List<B> =
    arrow.core.extensions.list.monad.List.monad().run {
  arrow.core.ListK(this@select).select<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<B>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: ListKMonad = object : arrow.core.extensions.ListKMonad {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
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
  inline fun monad(): ListKMonad = monad_singleton}
