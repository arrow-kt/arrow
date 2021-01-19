package arrow.core.extensions.either.foldable

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.None
import arrow.core.Some
import arrow.core.extensions.EitherFoldable
import arrow.core.fix
import arrow.core.orNull
import arrow.core.right
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
internal val foldable_singleton: EitherFoldable<Any?> = object : EitherFoldable<Any?> {}

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("foldLeft(arg1, arg2)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  fix().foldLeft(arg1, arg2)

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("foldRight(arg1, arg2)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.foldRight(
  arg1: Eval<B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<B> =
  fix().foldRight(arg1, arg2)

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("fold({ arg1.empty() }, { it })"))
fun <L, A> Kind<Kind<ForEither, L>, A>.fold(arg1: Monoid<A>): A =
  fix().fold({ arg1.empty() }, { it })

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Option.fromNullable(map(arg1).orNull()), { it })", "arrow.core.Option"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.reduceLeftToOption(
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Eval.now(Option.fromNullable(map(arg1).orNull()))", "arrow.core.Option", "arrow.core.Eval"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.reduceRightToOption(
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Option.fromNullable(orNull())", "arrow.core.Option"))
fun <L, A> Kind<Kind<ForEither, L>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  Option.fromNullable(orNull())

@JvmName("reduceRightOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Eval.now(Option.fromNullable(orNull()))", "arrow.core.Option", "arrow.core.Eval"))
fun <L, A> Kind<Kind<ForEither, L>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  Eval.now(Option.fromNullable(orNull()))

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("fold({ arg1.empty() }, { it })"))
fun <L, A> Kind<Kind<ForEither, L>, A>.combineAll(arg1: Monoid<A>): A =
  fix().fold({ arg1.empty() }, { it })

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("foldMap(arg1, arg2)"))
fun <L, A, B> Kind<Kind<ForEither, L>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  fix().foldMap(arg1, arg2)

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg1.empty().right()", "arrow.core.right"))
fun <L, A> orEmpty(arg0: Applicative<Kind<ForEither, L>>, arg1: Monoid<A>): Either<L, A> =
  arg1.empty().right()

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverse_ or traverseValidated_ from arrow.core.*")
fun <L, G, A, B> Kind<Kind<ForEither, L>, A>.traverse_(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Unit> = arrow.core.Either.foldable<L>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequence_ or sequenceValidated_ from arrow.core.*")
fun <L, G, A> Kind<Kind<ForEither, L>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.Either.foldable<L>().run {
    this@sequence_.sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
  }

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Option.fromNullable(findOrNull(arg1))", "arrow.core.Option"))
fun <L, A> Kind<Kind<ForEither, L>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(fix().findOrNull(arg1))

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("exists(arg1)"))
fun <L, A> Kind<Kind<ForEither, L>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  fix().exists(arg1)

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("all(arg1)"))
fun <L, A> Kind<Kind<ForEither, L>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  fix().all(arg1)

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("all(arg1)"))
fun <L, A> Kind<Kind<ForEither, L>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  fix().all(arg1)

@JvmName("isEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("isEmpty()"))
fun <L, A> Kind<Kind<ForEither, L>, A>.isEmpty(): Boolean =
  fix().isEmpty()

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("isNotEmpty()"))
fun <L, A> Kind<Kind<ForEither, L>, A>.nonEmpty(): Boolean =
  fix().isNotEmpty()

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("isNotEmpty()"))
fun <L, A> Kind<Kind<ForEither, L>, A>.isNotEmpty(): Boolean =
  fix().isNotEmpty()

@JvmName("size")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("fold({ 0 }, { 1 })"))
fun <L, A> Kind<Kind<ForEither, L>, A>.size(arg1: Monoid<Long>): Long =
  fix().fold({ 0 }, { 1 })

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Validated")
fun <L, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForEither, L>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Either.foldable<L>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Validated")
fun <L, G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForEither, L>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Either.foldable<L>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Validated")
fun <L, G, A, B> Kind<Kind<ForEither, L>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Either.foldable<L>().run {
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
fun <L, A> Kind<Kind<ForEither, L>, A>.get(arg1: Long): Option<A> =
  if (arg1 < 0L) None else fix().fold({ None }, { if (arg1 == 0L) Some(it) else None })

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(orNull())", "arrow.core.Option"))
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOption(): Option<A> =
  Option.fromNullable(orNull())

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(findOrNull(arg1))", "arrow.core.Option"))
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(fix().findOrNull(arg1))

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(orNull())", "arrow.core.Option"))
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOrNone(): Option<A> =
  Option.fromNullable(orNull())

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("Option.fromNullable(findOrNull(arg1))", "arrow.core.Option"))
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(fix().findOrNull(arg1))

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated.", ReplaceWith("listOfNotNull(orNull())"))
fun <L, A> Kind<Kind<ForEither, L>, A>.toList(): List<A> =
  listOfNotNull(orNull())

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on Either")
inline fun <L> Companion.foldable(): EitherFoldable<L> = foldable_singleton as
  arrow.core.extensions.EitherFoldable<L>
