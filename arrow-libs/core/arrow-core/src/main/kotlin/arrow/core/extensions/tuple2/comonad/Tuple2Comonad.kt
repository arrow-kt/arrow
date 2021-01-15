package arrow.core.extensions.tuple2.comonad

import arrow.Kind
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Comonad
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
internal val comonad_singleton: Tuple2Comonad<Any?> = object : Tuple2Comonad<Any?> {}

@JvmName("coflatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "coflatMap(arg1)",
  "arrow.core.coflatMap"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.coflatMap(
  arg1: Function1<Kind<Kind<ForTuple2, F>, A>, B>
): Tuple2<F, B> = arrow.core.Tuple2.comonad<F>().run {
  this@coflatMap.coflatMap<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

@JvmName("extract")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "extract()",
  "arrow.core.extract"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.extract(): A = arrow.core.Tuple2.comonad<F>().run {
  this@extract.extract<A>() as A
}

@JvmName("duplicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "duplicate()",
  "arrow.core.duplicate"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.duplicate(): Tuple2<F, Tuple2<F, A>> =
    arrow.core.Tuple2.comonad<F>().run {
  this@duplicate.duplicate<A>() as arrow.core.Tuple2<F, arrow.core.Tuple2<F, A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <F> Companion.comonad(): Tuple2Comonad<F> = comonad_singleton as
    arrow.core.extensions.Tuple2Comonad<F>
