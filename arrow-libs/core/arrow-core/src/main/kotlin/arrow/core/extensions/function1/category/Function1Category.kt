package arrow.core.extensions.function1.category

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1
import arrow.core.Function1.Companion
import arrow.core.extensions.Function1Category
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val category_singleton: Function1Category = object :
    arrow.core.extensions.Function1Category {}

@JvmName("compose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "compose(arg1)",
  "arrow.core.compose"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<Kind<ForFunction1, B>, C>.compose(arg1: Kind<Kind<ForFunction1, A>, B>):
    Function1<A, C> = arrow.core.Function1.category().run {
  this@compose.compose<A, B, C>(arg1) as arrow.core.Function1<A, C>
}

@JvmName("andThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "andThen(arg1)",
  "arrow.core.andThen"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<Kind<ForFunction1, A>, B>.andThen(arg1: Kind<Kind<ForFunction1, B>, C>):
    Function1<A, C> = arrow.core.Function1.category().run {
  this@andThen.andThen<A, B, C>(arg1) as arrow.core.Function1<A, C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.category(): Function1Category = category_singleton
