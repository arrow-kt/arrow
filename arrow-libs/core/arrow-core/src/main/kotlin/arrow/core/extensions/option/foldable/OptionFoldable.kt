package arrow.core.extensions.option.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionFoldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: OptionFoldable = object : arrow.core.extensions.OptionFoldable {}

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
    "foldLeft(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.Option.foldable().run {
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
    "foldRight(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>):
  Eval<B> = arrow.core.Option.foldable().run {
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
    "combineAll(arg1)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.fold(arg1: Monoid<A>): A = arrow.core.Option.foldable().run {
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
    "Option.fromNullable(this.reduceOrNull(arg1, arg2))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.reduceLeftToOption(arg1: Function1<A, B>, arg2: Function2<B, A, B>):
  Option<B> = arrow.core.Option.foldable().run {
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
    "reduceRightEvalOrNull(arg1, arg2).map { Option.fromNullable(it) }",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.Option.foldable().run {
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
    "Option.fromNullable<A>(this.reduceOrNull<A, A>(::identity, arg1))",
    "arrow.core.Option",
    "arrow.core.identity",
    "arrow.core.reduceOrNull"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.Option.foldable().run {
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
    "reduceRightEvalOrNull<A, A>(::identity, arg1).map<Option<A>> { Option.fromNullable<A>(it) }",
    "arrow.core.Option",
    "arrow.core.identity",
    "arrow.core.reduceRightEvalOrNull"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  arrow.core.Option.foldable().run {
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
    "combineAll(arg1)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.combineAll(arg1: Monoid<A>): A = arrow.core.Option.foldable().run {
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
    "foldMap(arg1, arg2)",
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.Option.foldable().run {
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
    "Option(arg1.empty())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> orEmpty(arg0: Applicative<ForOption>, arg1: Monoid<A>): Option<A> = arrow.core.Option
  .foldable()
  .orEmpty<A>(arg0, arg1) as arrow.core.Option<A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with traverse_, traverseValidated_ or traverseEither_ from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForOption, A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>):
  Kind<G, Unit> = arrow.core.Option.foldable().run {
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
  "@extension kinded projected functions are deprecated. Replace with sequence_, sequenceValidated_ or sequenceEither_ from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A> Kind<ForOption, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.Option.foldable().run {
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
fun <A> Kind<ForOption, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Option.foldable().run {
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
    "exists(arg1)",
    "arrow.core.exists"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Option.foldable().run {
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
    "all(arg1)",
    "arrow.core.all"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Option.foldable().run {
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
    "all(arg1)",
    "arrow.core.all"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Option.foldable().run {
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
    "isEmpty()",
    "arrow.core.isEmpty"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.isEmpty(): Boolean = arrow.core.Option.foldable().run {
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
    "isNotEmpty()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.nonEmpty(): Boolean = arrow.core.Option.foldable().run {
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
    "isNotEmpty()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.isNotEmpty(): Boolean = arrow.core.Option.foldable().run {
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
    "fold({ 0 }, { 1 })"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.size(arg1: Monoid<Long>): Long = arrow.core.Option.foldable().run {
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
  "Applicative typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
fun <G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<ForOption, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Option.foldable().run {
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
  "Monad typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
fun <G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<ForOption, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Option.foldable().run {
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
  "Monad typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForOption, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Option.foldable().run {
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
    "if (arg1 < 0L) None else this.fold({ None }, { if(arg1 == 0L) Some<A>(it) else None })",
    "arrow.core.None",
    "arrow.core.Some"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.get(arg1: Long): Option<A> = arrow.core.Option.foldable().run {
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
fun <A> Kind<ForOption, A>.firstOption(): Option<A> = arrow.core.Option.foldable().run {
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
fun <A> Kind<ForOption, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Option.foldable().run {
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
fun <A> Kind<ForOption, A>.firstOrNone(): Option<A> = arrow.core.Option.foldable().run {
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
fun <A> Kind<ForOption, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Option.foldable().run {
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
    "toList()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.toList(): List<A> = arrow.core.Option.foldable().run {
  this@toList.toList<A>() as kotlin.collections.List<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Foldable typeclass is deprecated. Use concrete methods on NonEmptyList",
  level = DeprecationLevel.WARNING
)
inline fun Companion.foldable(): OptionFoldable = foldable_singleton
