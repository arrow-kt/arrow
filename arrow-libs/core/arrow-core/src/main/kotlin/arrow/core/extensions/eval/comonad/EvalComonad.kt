package arrow.core.extensions.eval.comonad

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Companion
import arrow.core.ForEval
import arrow.core.extensions.EvalComonad
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val comonad_singleton: EvalComonad = object : arrow.core.extensions.EvalComonad {}

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
fun <A, B> Kind<ForEval, A>.coflatMap(arg1: Function1<Kind<ForEval, A>, B>): Eval<B> =
    arrow.core.Eval.comonad().run {
  this@coflatMap.coflatMap<A, B>(arg1) as arrow.core.Eval<B>
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
fun <A> Kind<ForEval, A>.extract(): A = arrow.core.Eval.comonad().run {
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
fun <A> Kind<ForEval, A>.duplicate(): Eval<Eval<A>> = arrow.core.Eval.comonad().run {
  this@duplicate.duplicate<A>() as arrow.core.Eval<arrow.core.Eval<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.comonad(): EvalComonad = comonad_singleton
