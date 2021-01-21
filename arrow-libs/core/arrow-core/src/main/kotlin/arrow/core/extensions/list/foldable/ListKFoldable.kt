package arrow.core.extensions.list.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.foldRight as _foldRight
import arrow.core.ForListK
import arrow.core.Option
import arrow.core.combineAll as _combineAll
import arrow.core.foldMap as _foldMap
import arrow.core.extensions.ListKFoldable
import arrow.core.reduceOrNull
import arrow.core.reduceRightEvalOrNull
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.collections.all as _all
import kotlin.collections.isNotEmpty as _isNotEmpty
import kotlin.Boolean
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("fold(arg1, arg2)"))
fun <A, B> List<A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  fold(arg1, arg2)

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("foldRight(arg1, arg2)", "arrow.core.foldRight"))
fun <A, B> List<A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<B> =
  _foldRight(arg1, arg2)

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("combineAll(arg1)", "arrow.core.combineAll"))
fun <A> List<A>.fold(arg1: Monoid<A>): A =
  _combineAll(arg1)

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(reduceOrNull(arg1, arg2))",
    "arrow.core.reduceOrNull",
    "arrow.core.Option"
  )
)
fun <A, B> List<A>.reduceLeftToOption(arg1: Function1<A, B>, arg2: Function2<B, A, B>): Option<B> =
  Option.fromNullable(reduceOrNull(arg1, arg2))

@JvmName("reduceRightToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "reduceRightOrNull(arg1, arg2).map { Option.fromNullable(it) }",
    "arrow.core.reduceRightOrNull",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> List<A>.reduceRightToOption(arg1: Function1<A, B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<Option<B>> =
  reduceRightEvalOrNull(arg1, arg2).map { Option.fromNullable(it) }

@JvmName("reduceLeftOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith(" Option.fromNullable(reduceOrNull({ it }, arg1))", "arrow.core.reduceOrNull", "arrow.core.Option"))
fun <A> List<A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  Option.fromNullable(reduceOrNull({ it }, arg1))

@JvmName("reduceRightOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "reduceOrNull({ it }, arg2).map { Option.fromNullable(it) }",
    "arrow.core.reduceOrNull",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> List<A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  reduceRightEvalOrNull({ it }, arg1).map { Option.fromNullable(it) }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("combineAll(arg1)", "arrow.core.combineAll"))
fun <A> List<A>.combineAll(arg1: Monoid<A>): A =
  _combineAll(arg1)

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("foldMap(arg1, arg2)", "arrow.core.foldMap"))
fun <A, B> List<A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  _foldMap(arg1, arg2)

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listOf(arg1.empty())"))
fun <A> orEmpty(arg0: Applicative<ForListK>, arg1: Monoid<A>): List<A> =
  listOf(arg1.empty())

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverseValidated_ or traverseEither_ from arrow.core.*")
fun <G, A, B> List<A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G, Unit> =
  arrow.core.extensions.list.foldable.List.foldable().run {
    arrow.core.ListK(this@traverse_).traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
  }

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceValidated_ or sequenceEither_ from arrow.core.*")
fun <G, A> List<Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.extensions.list.foldable.List.foldable().run {
    arrow.core.ListK(this@sequence_).sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
  }

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Option.fromNullable(firstOrNull(arg1))", "arrow.core.Option"))
fun <A> List<A>.find(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(firstOrNull(arg1))

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("any(arg1)"))
fun <A> List<A>.exists(arg1: Function1<A, Boolean>): Boolean =
  any(arg1)

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("all(arg1)"))
fun <A> List<A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  _all(arg1)

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("all(arg1)"))
fun <A> List<A>.all(arg1: Function1<A, Boolean>): Boolean =
  _all(arg1)

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("isNotEmpty()"))
fun <A> List<A>.nonEmpty(): Boolean =
  _isNotEmpty()

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("isNotEmpty()"))
fun <A> List<A>.isNotEmpty(): Boolean =
  _isNotEmpty()

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on List")
fun <G, A, B, AP : Applicative<G>, MO : Monoid<B>> List<A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldMapA).foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on List")
fun <G, A, B, MA : Monad<G>, MO : Monoid<B>> List<A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldMapM).foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on List")
fun <G, A, B> List<A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldM).foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Option.fromNullable(firstOrNull())", "arrow.core.Option"))
fun <A> List<A>.firstOption(): Option<A> =
  Option.fromNullable(firstOrNull())

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Option.fromNullable(firstOrNull(arg1))", "arrow.core.Option"))
fun <A> List<A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(firstOrNull(arg1))

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Option.fromNullable(firstOrNull())", "arrow.core.Option"))
fun <A> List<A>.firstOrNone(): Option<A> =
  Option.fromNullable(firstOrNull())

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Option.fromNullable(firstOrNull(arg1))", "arrow.core.Option"))
fun <A> List<A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(firstOrNull(arg1))

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("this"))
fun <A> List<A>.toList(): List<A> =
  this

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: ListKFoldable = object : arrow.core.extensions.ListKFoldable {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Foldable typeclasses is deprecated. Use concrete methods on List")
  inline fun foldable(): ListKFoldable = foldable_singleton
}
