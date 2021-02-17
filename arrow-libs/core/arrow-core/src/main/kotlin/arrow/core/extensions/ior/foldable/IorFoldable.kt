package arrow.core.extensions.ior.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.Option
import arrow.core.extensions.IorFoldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: IorFoldable<Any?> = object : IorFoldable<Any?> {}

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.foldLeft(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.Ior.foldable<L>().run {
    this@foldLeft.foldLeft<A, B>(arg1, arg2) as B
  }

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.foldRight(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.foldRight(
  arg1: Eval<B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<B> = arrow.core.Ior.foldable<L>().run {
  this@foldRight.foldRight<A, B>(arg1, arg2) as arrow.core.Eval<B>
}

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.fold({ arg1.empty() }, { it }, { _, b -> b })",
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.fold(arg1: Monoid<A>): A = arrow.core.Ior.foldable<L>().run {
  this@fold.fold<A>(arg1) as A
}

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.map(arg1).orNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.reduceLeftToOption(
  arg1: Function1<A, B>,
  arg2: Function2<B, A, B>
): Option<B> = arrow.core.Ior.foldable<L>().run {
  this@reduceLeftToOption.reduceLeftToOption<A, B>(arg1, arg2) as arrow.core.Option<B>
}

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
    "Eval.now(Option.fromNullable(this.map(arg1).orNull()))",
    "arrow.core.Eval",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.Ior.foldable<L>().run {
  this@reduceRightToOption.reduceRightToOption<A, B>(arg1, arg2) as
    arrow.core.Eval<arrow.core.Option<B>>
}

@JvmName("reduceLeftOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.orNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.Ior.foldable<L>().run {
    this@reduceLeftOption.reduceLeftOption<A>(arg1) as arrow.core.Option<A>
  }

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
    "Eval.now(Option.fromNullable(this.orNull()))",
    "arrow.core.Eval",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>):
  Eval<Option<A>> = arrow.core.Ior.foldable<L>().run {
    this@reduceRightOption.reduceRightOption<A>(arg1) as arrow.core.Eval<arrow.core.Option<A>>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.fold({ arg1.empty() }, { it }, { _, b -> b })"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.Ior.foldable<L>().run {
    this@combineAll.combineAll<A>(arg1) as A
  }

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.foldMap(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.Ior.foldable<L>().run {
    this@foldMap.foldMap<A, B>(arg1, arg2) as B
  }

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Ior.Right(arg1.empty)",
    "arrow.core.Ior"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> orEmpty(arg0: Applicative<Kind<ForIor, L>>, arg1: Monoid<A>): Ior<L, A> = arrow.core.Ior
  .foldable<L>()
  .orEmpty<A>(arg0, arg1) as arrow.core.Ior<L, A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with traverse_, traverseEither_ or traverseValidated_ from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <L, G, A, B> Kind<Kind<ForIor, L>, A>.traverse_(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Unit> = arrow.core.Ior.foldable<L>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with sequence_, sequenceEither_ or sequenceValidated_ from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <L, G, A> Kind<Kind<ForIor, L>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.Ior.foldable<L>().run {
    this@sequence_.sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
  }

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.findOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Ior.foldable<L>().run {
    this@find.find<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.exists(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Ior.foldable<L>().run {
    this@exists.exists<A>(arg1) as kotlin.Boolean
  }

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.all(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Ior.foldable<L>().run {
    this@forAll.forAll<A>(arg1) as kotlin.Boolean
  }

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.all(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Ior.foldable<L>().run {
    this@all.all<A>(arg1) as kotlin.Boolean
  }

@JvmName("isEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.isEmpty()"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.isEmpty(): Boolean = arrow.core.Ior.foldable<L>().run {
  this@isEmpty.isEmpty<A>() as kotlin.Boolean
}

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.isNotEmpty()"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.nonEmpty(): Boolean = arrow.core.Ior.foldable<L>().run {
  this@nonEmpty.nonEmpty<A>() as kotlin.Boolean
}

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.isNotEmpty()"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.isNotEmpty(): Boolean = arrow.core.Ior.foldable<L>().run {
  this@isNotEmpty.isNotEmpty<A>() as kotlin.Boolean
}

@JvmName("size")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.fold({ 0 }, { 1 }, { 1 })"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.size(arg1: Monoid<Long>): Long =
  arrow.core.Ior.foldable<L>().run {
    this@size.size<A>(arg1) as kotlin.Long
  }

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
fun <L, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForIor, L>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Ior.foldable<L>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Monad typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
fun <L, G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForIor, L>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Ior.foldable<L>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Monad typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
fun <L, G, A, B> Kind<Kind<ForIor, L>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Ior.foldable<L>().run {
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
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "if (arg1 == 0L) this.fold({ None }, { Some(it) }, { _, b -> Some(b) }) else None",
    "arrow.core.None",
    "arrow.core.Some"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.get(arg1: Long): Option<A> = arrow.core.Ior.foldable<L>().run {
  this@get.get<A>(arg1) as arrow.core.Option<A>
}

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.orNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.firstOption(): Option<A> = arrow.core.Ior.foldable<L>().run {
  this@firstOption.firstOption<A>() as arrow.core.Option<A>
}

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.findOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Ior.foldable<L>().run {
    this@firstOption.firstOption<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.orNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.firstOrNone(): Option<A> = arrow.core.Ior.foldable<L>().run {
  this@firstOrNone.firstOrNone<A>() as arrow.core.Option<A>
}

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Option.fromNullable(this.findOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Ior.foldable<L>().run {
    this@firstOrNone.firstOrNone<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "listOfNotNull(this.orNull())"
  ),
  DeprecationLevel.WARNING
)
fun <L, A> Kind<Kind<ForIor, L>, A>.toList(): List<A> = arrow.core.Ior.foldable<L>().run {
  this@toList.toList<A>() as kotlin.collections.List<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Foldable typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
inline fun <L> Companion.foldable(): IorFoldable<L> = foldable_singleton as
  arrow.core.extensions.IorFoldable<L>
