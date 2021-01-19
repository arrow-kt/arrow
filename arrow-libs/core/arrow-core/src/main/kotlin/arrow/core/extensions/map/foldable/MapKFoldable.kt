package arrow.core.extensions.map.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForMapK
import arrow.core.Option
import arrow.core.combineAll
import arrow.core.extensions.MapKFoldable
import arrow.core.fold
import arrow.core.foldMap
import arrow.core.reduceOrNull
import arrow.core.reduceRightEvalOrNull
import arrow.core.foldLeft as _foldLeft
import arrow.core.foldRight as _foldRight
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import java.lang.IllegalStateException
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.JvmName
import kotlin.collections.isNotEmpty as _isNotEmpty

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
fun <K, A, B> Map<K, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  _foldLeft(arg1) { acc, (_, a) -> arg2(acc, a) }

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
fun <K, A, B> Map<K, A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<B> =
  values.toList()._foldRight(arg1, arg2)

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
fun <K, A> Map<K, A>.fold(arg1: Monoid<A>): A =
  values.fold(arg1)

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
    "arrow.core.reduceNullable", "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.reduceLeftToOption(arg1: Function1<A, B>, arg2: Function2<B, A, B>): Option<B> =
  Option.fromNullable(values.reduceOrNull(arg1, arg2))

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
fun <K, A, B> Map<K, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> =
  values.toList().reduceRightEvalOrNull(arg1, arg2).map { Option.fromNullable(it) }

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
    "arrow.core.reduceNullable", "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  Option.fromNullable(values.reduceOrNull({ it }, arg1))

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
fun <K, A> Map<K, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  values.toList().reduceRightEvalOrNull({ it }, arg1).map { Option.fromNullable(it) }

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
fun <K, A> Map<K, A>.combineAll(arg1: Monoid<A>): A =
  values.combineAll(arg1)

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
fun <K, A, B> Map<K, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  values.foldMap(arg1, arg2)

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("MapK doesn't have an applicative instance. This signature is invalid.")
fun <K, A> orEmpty(arg0: Applicative<Kind<ForMapK, K>>, arg1: Monoid<A>): Map<K, A> =
  throw IllegalStateException("MapK doesn't have an applicative instance. This signature is invalid.")

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with traverseValidated_ or traverseEither_ from arrow.core.*")
fun <K, G, A, B> Map<K, A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G,
  Unit> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@traverse_).traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceValidated_ or sequenceEither_ from arrow.core.*")
fun <K, G, A> Map<K, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@sequence_).sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
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
fun <K, A> Map<K, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(values.firstOrNull(arg1))

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
fun <K, A> Map<K, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  values.any(arg1)

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
fun <K, A> Map<K, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  values.all(arg1)

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
fun <K, A> Map<K, A>.all(arg1: Function1<A, Boolean>): Boolean =
  values.all(arg1)

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
fun <K, A> Map<K, A>.nonEmpty(): Boolean =
  _isNotEmpty()

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
fun <K, A> Map<K, A>.isNotEmpty(): Boolean =
  _isNotEmpty()

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Map")
fun <K, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Map<K, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@foldMapA).foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Map")
fun <K, G, A, B, MA : Monad<G>, MO : Monoid<B>> Map<K, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@foldMapM).foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Applicative typeclass is deprecated. Use concrete methods on Map")
fun <K, G, A, B> Map<K, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@foldM).foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
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
    "Option.fromNullable(values.firstOrNull())",
    "arrow.core.Option"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.firstOption(): Option<A> =
  Option.fromNullable(values.firstOrNull())

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
fun <K, A> Map<K, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(values.firstOrNull(arg1))

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
fun <K, A> Map<K, A>.firstOrNone(): Option<A> =
  Option.fromNullable(values.firstOrNull())

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
fun <K, A> Map<K, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  Option.fromNullable(values.firstOrNull(arg1))

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
fun <K, A> Map<K, A>.toList(): List<A> =
  values.toList()

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: MapKFoldable<Any?> = object : MapKFoldable<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Foldable typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> foldable(): MapKFoldable<K> = foldable_singleton as
    arrow.core.extensions.MapKFoldable<K>
}
