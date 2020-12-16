package arrow.core.extensions.validated.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForValidated
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.fold as _fold
import arrow.core.combineAll as _combineAll
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedFoldable
import arrow.core.fix
import arrow.core.orNull
import arrow.core.valid
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Boolean
import kotlin.Function1
import kotlin.Function2
import kotlin.Long
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: ValidatedFoldable<Any?> = object : ValidatedFoldable<Any?> {}

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("foldLeft(arg1, arg2)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  fix().foldLeft(arg1, arg2)

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("foldRight(arg1, arg2)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<B> =
  fix().foldRight(arg1, arg2)

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("fold(arg1)", "arrow.core.fold"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.fold(arg1: Monoid<A>): A =
  fix()._fold(arg1)

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Option.fromNullable(map(arg1).orNull())", "arrow.core.Option", "arrow.core.orNull"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.reduceLeftToOption(
  arg1: Function1<A, B>,
  arg2: Function2<B, A, B>
): Option<B> =
  Option.fromNullable(fix().map(arg1).orNull())

@JvmName("reduceRightToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Eval.now(Option.fromNullable(map(arg1).orNull()))", "arrow.core.Option", "arrow.core.Eval", "arrow.core.orNull"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> =
  Eval.now(Option.fromNullable(fix().map(arg1).orNull()))

@JvmName("reduceLeftOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Option.fromNullable(orNull())", "arrow.core.Option", "arrow.core.orNull"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  Option.fromNullable(orNull())

@JvmName("reduceRightOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Eval.now(Option.fromNullable(orNull()))", "arrow.core.Option", "arrow.core.Eval", "arrow.core.orNull"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  Eval.now(Option.fromNullable(orNull()))

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("combineAll(arg1)", "arrow.core.combineAll"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.combineAll(arg1: Monoid<A>): A =
  fix()._combineAll(arg1)

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("foldMap(arg1, arg2)"))
fun <E, A, B> Kind<Kind<ForValidated, E>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  fix().foldMap(arg1, arg2)

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg1.empty().valid()", "arrow.core.valid"))
fun <E, A> orEmpty(arg0: Applicative<Kind<ForValidated, E>>, arg1: Monoid<A>): Validated<E, A> =
  arg1.empty().valid()

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverse_ or traverseEither_ from arrow.core.*")
fun <E, G, A, B> Kind<Kind<ForValidated, E>, A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G, Unit> = arrow.core.Validated.foldable<E>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequence_ or sequenceEither_ from arrow.core.*")
fun <E, G, A> Kind<Kind<ForValidated, E>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> = arrow.core.Validated.foldable<E>().run {
  this@sequence_.sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(findOrNull(arg1))", "arrow.core.Option"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(fix().findOrNull(arg1))

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("exists(arg1)"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  fix().exist(arg1)

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("all(arg1)"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  fix().all(arg1)

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("all(arg1)"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  fix().all(arg1)

@JvmName("isEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("isEmpty()"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.isEmpty(): Boolean =
  fix().isEmpty()

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("isNotEmpty()"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.nonEmpty(): Boolean =
  fix().isNotEmpty()

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("isNotEmpty()"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.isNotEmpty(): Boolean =
  fix().isNotEmpty()

@JvmName("size")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("fold({ 0 }, { 1 })"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.size(arg1: Monoid<Long>): Long =
  fix().fold({ 0 }, { 1 })

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclasses is deprecated. Use concrete methods on Validated")
fun <E, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForValidated, E>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Validated.foldable<E>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Monad typeclasses is deprecated. Use concrete methods on Validated")
fun <E, G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForValidated, E>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Validated.foldable<E>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Monad typeclasses is deprecated. Use concrete methods on Validated")
fun <E, G, A, B> Kind<Kind<ForValidated, E>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Validated.foldable<E>().run {
  this@foldM.foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("get")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated.",
  ReplaceWith("if (arg1 < 0L) None else fix().fold({ None }, { if(arg1 == 0L) Some(it) else None })", "arrow.core.None", "arrow.core.Some")
)
fun <E, A> Kind<Kind<ForValidated, E>, A>.get(arg1: Long): Option<A> =
  if (arg1 < 0L) None else fix().fold({ None }, { if (arg1 == 0L) Some(it) else None })

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(orNull())", "arrow.core.Option"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.firstOption(): Option<A> =
  arrow.core.Validated.foldable<E>().run {
    this@firstOption.firstOption<A>() as arrow.core.Option<A>
  }

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(findOrNull(arg1))", "arrow.core.Option"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(fix().findOrNull(arg1))

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(orNull())", "arrow.core.Option"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.firstOrNone(): Option<A> =
  Option.fromNullable(orNull())

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(findOrNull(arg1))", "arrow.core.Option"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(fix().findOrNull(arg1))

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("listOfNotNull(orNull())"))
fun <E, A> Kind<Kind<ForValidated, E>, A>.toList(): List<A> =
  listOfNotNull(orNull())

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on Validated")
inline fun <E> Companion.foldable(): ValidatedFoldable<E> = foldable_singleton as
  arrow.core.extensions.ValidatedFoldable<E>
