package arrow.core.extensions.sequence.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.extensions.SequenceKFoldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.sequences.Sequence

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
    "this.fold(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@foldLeft).foldLeft<A, B>(arg1, arg2) as B
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
    "this.foldRight(arg1, arg2)",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<B> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@foldRight).foldRight<A, B>(arg1, arg2) as arrow.core.Eval<B>
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
    "this.fold(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.fold(arg1: Monoid<A>): A =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@fold).fold<A>(arg1) as A
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
fun <A, B> Sequence<A>.reduceLeftToOption(arg1: Function1<A, B>, arg2: Function2<B, A, B>):
  Option<B> = arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@reduceLeftToOption).reduceLeftToOption<A, B>(arg1, arg2) as
      arrow.core.Option<B>
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
    "this.reduceRightEvalOrNull(arg1, arg2).map { Option.fromNullable(it) }",
    "arrow.core.Option",
    "arrow.core.reduceRightEvalOrNull"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
  arrow.core.SequenceK(this@reduceRightToOption).reduceRightToOption<A, B>(arg1, arg2) as
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
    "Option.fromNullable(this.reduceOrNull({ it }, arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@reduceLeftOption).reduceLeftOption<A>(arg1) as arrow.core.Option<A>
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
    "reduceRightOption(arg1)",
    "arrow.core.reduceRightOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@reduceRightOption).reduceRightOption<A>(arg1) as
      arrow.core.Eval<arrow.core.Option<A>>
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
    "this.combineAll(arg1)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@combineAll).combineAll<A>(arg1) as A
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
    "this.foldMap(arg1, arg2)",
    "arrow.core.foldMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@foldMap).foldMap<A, B>(arg1, arg2) as B
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
    "sequenceOf(arg1.empty())"
  ),
  DeprecationLevel.WARNING
)
fun <A> orEmpty(arg0: Applicative<ForSequenceK>, arg1: Monoid<A>): Sequence<A> =
  arrow.core.extensions.sequence.foldable.Sequence
    .foldable()
    .orEmpty<A>(arg0, arg1) as kotlin.sequences.Sequence<A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with traverseEither_ or traverseValidated_ from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Sequence<A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G,
  Unit> = arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
  arrow.core.SequenceK(this@traverse_).traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with sequenceEither_ or sequenceValidated_ from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A> Sequence<Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@sequence_).sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
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
    "Option.fromNullable(this.firstOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@find).find<A>(arg1) as arrow.core.Option<A>
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
    "this.any(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@exists).exists<A>(arg1) as kotlin.Boolean
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
fun <A> Sequence<A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@forAll).forAll<A>(arg1) as kotlin.Boolean
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
fun <A> Sequence<A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@all).all<A>(arg1) as kotlin.Boolean
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
    "this.none()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.isEmpty(): Boolean =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@isEmpty).isEmpty<A>() as kotlin.Boolean
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
    "this.any()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.nonEmpty(): Boolean =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@nonEmpty).nonEmpty<A>() as kotlin.Boolean
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
    "this.any()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.isNotEmpty(): Boolean =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@isNotEmpty).isNotEmpty<A>() as kotlin.Boolean
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
    "this.count()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.size(arg1: Monoid<Long>): Long =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@size).size<A>(arg1) as kotlin.Long
  }

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
fun <G, A, B, AP : Applicative<G>, MO : Monoid<B>> Sequence<A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
  arrow.core.SequenceK(this@foldMapA).foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G,
    B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Monad typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
fun <G, A, B, MA : Monad<G>, MO : Monoid<B>> Sequence<A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
  arrow.core.SequenceK(this@foldMapM).foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G,
    B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Monad typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Sequence<A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
  arrow.core.SequenceK(this@foldM).foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
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
    "Option.fromNullable(this.elementAtOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.get(arg1: Long): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@get).get<A>(arg1) as arrow.core.Option<A>
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
    "Option.fromNullable(this.firstOrNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.firstOption(): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@firstOption).firstOption<A>() as arrow.core.Option<A>
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
    "Option.fromNullable(this.firstOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@firstOption).firstOption<A>(arg1) as arrow.core.Option<A>
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
    "Option.fromNullable(this.firstOrNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.firstOrNone(): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@firstOrNone).firstOrNone<A>() as arrow.core.Option<A>
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
    "Option.fromNullable(this.firstOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@firstOrNone).firstOrNone<A>(arg1) as arrow.core.Option<A>
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
    "this.toList()"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.toList(): List<A> =
  arrow.core.extensions.sequence.foldable.Sequence.foldable().run {
    arrow.core.SequenceK(this@toList).toList<A>() as kotlin.collections.List<A>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: SequenceKFoldable = object :
  arrow.core.extensions.SequenceKFoldable {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Foldable typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun foldable(): SequenceKFoldable = foldable_singleton
}
