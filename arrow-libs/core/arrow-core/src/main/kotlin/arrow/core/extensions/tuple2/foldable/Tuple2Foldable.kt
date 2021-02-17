package arrow.core.extensions.tuple2.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForTuple2
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Foldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
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
internal val foldable_singleton: Tuple2Foldable<Any?> = object : Tuple2Foldable<Any?> {}

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg2(arg1, this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("arg2(arg1, lb)"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.foldRight(
  arg1: Eval<B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<B> = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("this.b"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.fold(arg1: Monoid<A>): A =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("Option(arg1(this.b))", "arrow.core.Option"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.reduceLeftToOption(
  arg1: Function1<A, B>,
  arg2: Function2<B, A, B>
): Option<B> = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("Option(arg1(this.b))", "arrow.core.Option"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("Option(arg1(this.b))", "arrow.core.Option"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("Option(arg1(this.b))", "arrow.core.Option"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>):
  Eval<Option<A>> = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("this.b"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("arg2(this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("arg0.just(arg1.empty())"),
  DeprecationLevel.WARNING
)
fun <F, A> orEmpty(arg0: Applicative<Kind<ForTuple2, F>>, arg1: Monoid<A>): Tuple2<F, A> =
  arrow.core.Tuple2
    .foldable<F>()
    .orEmpty<A>(arg0, arg1) as arrow.core.Tuple2<F, A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Pair")
fun <F, G, A, B> Kind<Kind<ForTuple2, F>, A>.traverse_(
  arg1: Applicative<G>,
  arg2: Function1<A,
    Kind<G, B>>
): Kind<G, Unit> = arrow.core.Tuple2.foldable<F>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Pair")
fun <F, G, A> Kind<Kind<ForTuple2, F>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.Tuple2.foldable<F>().run {
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
    "if(arg2(this.b)) Some(this.b) else None",
    "arrow.core.Some",
    "arrow.core.None"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("arg1(this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("arg1(this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("arg1(this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("false"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.isEmpty(): Boolean = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("true"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.nonEmpty(): Boolean = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("true"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.isNotEmpty(): Boolean = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("1"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.size(arg1: Monoid<Long>): Long =
  arrow.core.Tuple2.foldable<F>().run {
    this@size.size<A>(arg1) as kotlin.Long
  }

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Pair")
fun <F, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForTuple2, F>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Tuple2.foldable<F>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Pair")
fun <F, G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForTuple2, F>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Tuple2.foldable<F>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Monad typeclasses is deprecated. Use concrete methods on Pair")
fun <F, G, A, B> Kind<Kind<ForTuple2, F>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("if(arg1 == 0L) Some(this.b) else None", "arrow.core.Some", "arrow.core.None"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.get(arg1: Long): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("Some(this.b)", "arrow.core.Some"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.firstOption(): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("if(arg1(this.b)) Some(this.b) else None", "arrow.core.Some", "arrow.core.None"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("Some(this.b)", "arrow.core.Some"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.firstOrNone(): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("if(arg1(this.b)) Some(this.b) else None", "arrow.core.Some", "arrow.core.None"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Tuple2.foldable<F>().run {
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
  ReplaceWith("listOf(this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.toList(): List<A> = arrow.core.Tuple2.foldable<F>().run {
  this@toList.toList<A>() as kotlin.collections.List<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on Pair")
inline fun <F> Companion.foldable(): Tuple2Foldable<F> = foldable_singleton as
  arrow.core.extensions.Tuple2Foldable<F>
