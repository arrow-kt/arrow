package arrow.core.extensions.eval.applicative

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Companion
import arrow.core.ForEval
import arrow.core.extensions.EvalApplicative
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: EvalApplicative = object : arrow.core.extensions.EvalApplicative
    {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "just()",
  "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <A> A.just(): Eval<A> = arrow.core.Eval.applicative().run {
  this@just.just<A>() as arrow.core.Eval<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unit()",
  "arrow.core.Eval.unit"
  ),
  DeprecationLevel.WARNING
)
fun unit(): Eval<Unit> = arrow.core.Eval
   .applicative()
   .unit() as arrow.core.Eval<kotlin.Unit>

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
fun <A, B> Kind<ForEval, A>.map(arg1: Function1<A, B>): Eval<B> =
    arrow.core.Eval.applicative().run {
  this@map.map<A, B>(arg1) as arrow.core.Eval<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "replicate(arg1)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForEval, A>.replicate(arg1: Int): Eval<List<A>> = arrow.core.Eval.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.core.Eval<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "replicate(arg1, arg2)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForEval, A>.replicate(arg1: Int, arg2: Monoid<A>): Eval<A> =
    arrow.core.Eval.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Eval<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.applicative(): EvalApplicative = applicative_singleton
