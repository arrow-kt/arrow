package arrow.core.extensions.mapk.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.Option
import arrow.core.extensions.MapKFoldable
import arrow.core.fix
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
internal val foldable_singleton: MapKFoldable<Any?> = object : MapKFoldable<Any?> {}

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
    "foldLeft(arg1) { acc, (_, a) -> arg2(acc, a) }",
    "arrow.core.foldLeft"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.MapK.foldable<K>().run {
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
    "values.toList().foldRight(arg1, arg2)",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.foldRight(
  arg1: Eval<B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<B> = arrow.core.MapK.foldable<K>().run {
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
    "values.fold(arg1)",
    "arrow.core.fold"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.fold(arg1: Monoid<A>): A = arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.reduceOrNull(arg1, arg2))",
    "arrow.core.reduceOrNull",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.reduceLeftToOption(
  arg1: Function1<A, B>,
  arg2: Function2<B, A, B>
): Option<B> = arrow.core.MapK.foldable<K>().run {
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
    "values.toList().reduceRightEvalOrNull(arg1, arg2).map { Option.fromNullable(it) }",
    "arrow.core.reduceRightNullable", "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.reduceOrNull({ it }, arg1))",
    "arrow.core.reduceOrNull", "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.MapK.foldable<K>().run {
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
    "values.toList().reduceRightEvalOrNull({ it }, arg1).map { Option.fromNullable(it) }",
    "arrow.core.reduceRightNullable", "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>):
  Eval<Option<A>> = arrow.core.MapK.foldable<K>().run {
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
    "values.combineAll(arg1)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.MapK.foldable<K>().run {
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
    "values.foldMap(arg1, arg2)",
    "arrow.core.foldMap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.MapK.foldable<K>().run {
    this@foldMap.foldMap<A, B>(arg1, arg2) as B
  }

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("MapK doesn't have an applicative instance. This signature is invalid.")
fun <K, A> orEmpty(arg0: Applicative<Kind<ForMapK, K>>, arg1: Monoid<A>): MapK<K, A> =
  arrow.core.MapK
    .foldable<K>()
    .orEmpty<A>(arg0, arg1) as arrow.core.MapK<K, A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverseValidated_ or traverseEither_ from arrow.core.*")
fun <K, G, A, B> Kind<Kind<ForMapK, K>, A>.traverse_(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Unit> = arrow.core.MapK.foldable<K>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceValidated_ or sequenceEither_ from arrow.core.*")
fun <K, G, A> Kind<Kind<ForMapK, K>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.firstOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("values.any(arg1)"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("values.all(arg1)"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("values.all(arg1)"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("isEmpty()"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.isEmpty(): Boolean = arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("isNotEmpty()"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.nonEmpty(): Boolean = arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("isNotEmpty()"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.isNotEmpty(): Boolean = arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("size.toLong()"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.size(arg1: Monoid<Long>): Long =
  fix().size.toLong()

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Map")
fun <K, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForMapK, K>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.MapK.foldable<K>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Map")
fun <K, G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForMapK, K>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.MapK.foldable<K>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Map")
fun <K, G, A, B> Kind<Kind<ForMapK, K>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.toList().get(arg1.toInt()))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.get(arg1: Long): Option<A> =
  Option.fromNullable(fix().values.toList().get(arg1.toInt()))

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
    "Option.fromNullable(values.firstOrNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.firstOption(): Option<A> = arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.firstOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.firstOrNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.firstOrNone(): Option<A> = arrow.core.MapK.foldable<K>().run {
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
    "Option.fromNullable(values.firstOrNull(arg1))",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.MapK.foldable<K>().run {
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
  ReplaceWith("values.toList()"),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.toList(): List<A> = arrow.core.MapK.foldable<K>().run {
  this@toList.toList<A>() as kotlin.collections.List<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Foldable typeclasses is deprecated. Use concrete methods on Map")
inline fun <K> Companion.foldable(): MapKFoldable<K> = foldable_singleton as
  arrow.core.extensions.MapKFoldable<K>
