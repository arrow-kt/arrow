package arrow.core.extensions.const.foldable

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.Eval
import arrow.core.ForConst
import arrow.core.Option
import arrow.core.extensions.ConstFoldable
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
internal val foldable_singleton: ConstFoldable<Any?> = object : ConstFoldable<Any?> {}

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1"),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.Const.foldable<A>().run {
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
  ReplaceWith("arg1"),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.foldRight(
  arg1: Eval<B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<B> = arrow.core.Const.foldable<A>().run {
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
  ReplaceWith("arg1.empty()"),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.fold(arg1: Monoid<A>): A = arrow.core.Const.foldable<A>().run {
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
    "Option.empty()",
    "arrow.core.empty",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.reduceLeftToOption(
  arg1: Function1<A, B>,
  arg2: Function2<B, A, B>
): Option<B> = arrow.core.Const.foldable<A>().run {
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
    "Eval.Now<Option<B>>(Option.empty())",
    "arrow.core.empty",
    "arrow.core.Option",
    "arrow.core.Eval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> =
  arrow.core.Const.foldable<A>().run {
    this@reduceRightToOption.reduceRightToOption<A, B>(arg1, arg2) as arrow.core.Eval<arrow.core.Option<B>>
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
    "Option.empty()",
    "arrow.core.empty",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.Const.foldable<A>().run {
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
    "Eval.Now<Option<B>>(Option.empty())",
    "arrow.core.empty",
    "arrow.core.Option",
    "arrow.core.Eval"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>):
  Eval<Option<A>> = arrow.core.Const.foldable<A>().run {
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
  ReplaceWith("arg1.empty()"),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.Const.foldable<A>().run {
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
  ReplaceWith("arg1.empty()"),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.Const.foldable<A>().run {
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
  ReplaceWith("arg1.empty()"),
  DeprecationLevel.WARNING
)
fun <A> orEmpty(arg0: Applicative<Kind<ForConst, A>>, arg1: Monoid<A>): Const<A, A> =
  arrow.core.Const
    .foldable<A>()
    .orEmpty<A>(arg0, arg1) as arrow.core.Const<A, A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A, G, B> Kind<Kind<ForConst, A>, A>.traverse_(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Unit> = arrow.core.Const.foldable<A>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A, G> Kind<Kind<ForConst, A>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.now<Option<A>>(None)) { a, lb -> if (arg1(a)) Eval.now(Some(a)) else lb }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.False) { a, lb -> if (arg1(a)) Eval.True else lb }.value()",
    "arrow.core.exists"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.True) { a, lb -> if (arg1(a)) lb else Eval.False }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.True) { a, lb -> if (arg1(a)) lb else Eval.False }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.True) { _, _ -> Eval.False }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.isEmpty(): Boolean =
  arrow.core.Const.foldable<A>().run {
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
    "!foldRight(Eval.True) { _, _ -> Eval.False }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.nonEmpty(): Boolean =
  arrow.core.Const.foldable<A>().run {
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
    "!foldRight(Eval.True) { _, _ -> Eval.False }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.isNotEmpty(): Boolean = arrow.core.Const.foldable<A>().run {
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
    "arg1.run { foldLeft(arg1.empty()) { 1 }",
    "arrow.core.size"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.size(arg1: Monoid<Long>): Long =
  arrow.core.Const.foldable<A>().run {
    this@size.size<A>(arg1) as kotlin.Long
  }

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A, G, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForConst, A>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Const.foldable<A>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A, G, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForConst, A>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Const.foldable<A>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A, G, B> Kind<Kind<ForConst, A>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Const.foldable<A>().run {
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
    "if (arg1 < 0L) None\n" +
      "else foldLeft<A, Either<A, Long>>(0L.right()) { i, a ->\n" +
      "        i.flatMap {\n" +
      "          if (it == arg1) Left(a)\n" +
      "          else Right(it + 1L)\n" +
      "        }\n" +
      "      }.swap().toOption()",
    "arrow.core.foldLeft"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.get(arg1: Long): Option<A> =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.now<Option<A>>(None)) { Eval.now(Some(a)) }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.firstOption(): Option<A> = arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.now<Option<A>>(None)) { a, lb -> if (arg1(a)) Eval.now(Some(a)) else lb }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.now<Option<A>>(None)) { Eval.now(Some(a)) }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.firstOrNone(): Option<A> = arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.now<Option<A>>(None)) { a, lb -> if (arg1(a)) Eval.now(Some(a)) else lb }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Const.foldable<A>().run {
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
    "foldRight(Eval.now(emptyList<A>())) { v, acc -> acc.map { listOf(v) + it } }.value()",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<Kind<ForConst, A>, A>.toList(): List<A> =
  arrow.core.Const.foldable<A>().run {
    this@toList.toList<A>() as kotlin.collections.List<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Foldable typeclass is deprecated. Use concrete methods on Const",
  level = DeprecationLevel.WARNING
)
inline fun <A> Companion.foldable(): ConstFoldable<A> = foldable_singleton as
  arrow.core.extensions.ConstFoldable<A>
