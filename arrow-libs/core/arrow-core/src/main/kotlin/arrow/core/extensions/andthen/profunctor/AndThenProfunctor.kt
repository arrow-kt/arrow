package arrow.core.extensions.andthen.profunctor

import arrow.Kind
import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.ForAndThen
import arrow.core.extensions.AndThenProfunctor
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val profunctor_singleton: AndThenProfunctor = object :
    arrow.core.extensions.AndThenProfunctor {}

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
fun <A, B, C, D> Kind<Kind<ForAndThen, A>, B>.dimap(arg1: Function1<C, A>, arg2: Function1<B, D>):
    AndThen<C, D> = arrow.core.AndThen.profunctor().run {
  this@dimap.dimap<A, B, C, D>(arg1, arg2) as arrow.core.AndThen<C, D>
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
fun <A, B, C> Kind<Kind<ForAndThen, A>, B>.lmap(arg1: Function1<C, A>): AndThen<C, B> =
    arrow.core.AndThen.profunctor().run {
  this@lmap.lmap<A, B, C>(arg1) as arrow.core.AndThen<C, B>
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
fun <A, B, D> Kind<Kind<ForAndThen, A>, B>.rmap(arg1: Function1<B, D>): AndThen<A, D> =
    arrow.core.AndThen.profunctor().run {
  this@rmap.rmap<A, B, D>(arg1) as arrow.core.AndThen<A, D>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.profunctor(): AndThenProfunctor = profunctor_singleton
