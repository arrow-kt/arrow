package arrow.core.extensions.function1.profunctor

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Profunctor
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val profunctor_singleton: Function1Profunctor = object :
    arrow.core.extensions.Function1Profunctor {}

@JvmName("dimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "dimap(arg1, arg2)",
  "arrow.core.dimap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> Kind<Kind<ForFunction1, A>, B>.dimap(arg1: Function1<C, A>, arg2: Function1<B, D>):
    arrow.core.Function1<C, D> = arrow.core.Function1.profunctor().run {
  this@dimap.dimap<A, B, C, D>(arg1, arg2) as arrow.core.Function1<C, D>
}

@JvmName("lmap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lmap(arg1)",
  "arrow.core.lmap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<Kind<ForFunction1, A>, B>.lmap(arg1: Function1<C, A>): arrow.core.Function1<C, B> =
  arrow.core.Function1.profunctor().run {
    this@lmap.lmap<A, B, C>(arg1) as arrow.core.Function1<C, B>
  }

@JvmName("rmap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "rmap(arg1)",
  "arrow.core.rmap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, D> Kind<Kind<ForFunction1, A>, B>.rmap(arg1: Function1<B, D>): arrow.core.Function1<A, D> =
  arrow.core.Function1.profunctor().run {
    this@rmap.rmap<A, B, D>(arg1) as arrow.core.Function1<A, D>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.profunctor(): Function1Profunctor = profunctor_singleton
