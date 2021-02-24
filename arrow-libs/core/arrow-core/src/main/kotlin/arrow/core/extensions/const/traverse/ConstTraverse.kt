package arrow.core.extensions.const.traverse

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.extensions.ConstTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val traverse_singleton: ConstTraverse<Any?> = object : ConstTraverse<Any?> {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <X, G, A, B> Kind<Kind<ForConst, X>, A>.traverse(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Kind<Kind<ForConst, X>, B>> =
  arrow.core.Const.traverse<X>().run {
    this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForConst, X>, B>>
  }

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <X, G, A> Kind<Kind<ForConst, X>, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G, Kind<Kind<ForConst, X>, A>> =
  arrow.core.Const.traverse<X>().run {
    this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForConst, X>, A>>
  }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "map(arg1)",
    "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForConst, X>, A>.map(arg1: Function1<A, B>): Const<X, B> =
  arrow.core.Const.traverse<X>().run {
    this@map.map<A, B>(arg1) as arrow.core.Const<X, B>
  }

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <X, G, A, B> Kind<Kind<ForConst, X>, A>.flatTraverse(
  arg1: Monad<Kind<ForConst, X>>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<Kind<ForConst, X>, B>>>
): Kind<G, Kind<Kind<ForConst, X>, B>> =
  arrow.core.Const.traverse<X>().run {
    this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForConst, X>, B>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Traverse typeclass is deprecated. Use concrete methods on Const",
  level = DeprecationLevel.WARNING
)
inline fun <X> Companion.traverse(): ConstTraverse<X> = traverse_singleton as
  arrow.core.extensions.ConstTraverse<X>
