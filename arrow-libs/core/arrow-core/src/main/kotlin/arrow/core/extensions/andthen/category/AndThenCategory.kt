package arrow.core.extensions.andthen.category

import arrow.Kind
import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.AndThenDeprecation
import arrow.core.ForAndThen
import arrow.core.extensions.AndThenCategory
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val category_singleton: AndThenCategory = object : arrow.core.extensions.AndThenCategory {}

@JvmName("compose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <A, B, C> Kind<Kind<ForAndThen, B>, C>.compose(arg1: Kind<Kind<ForAndThen, A>, B>): AndThen<A,
  C> = arrow.core.AndThen.category().run {
  this@compose.compose<A, B, C>(arg1) as arrow.core.AndThen<A, C>
}

@JvmName("andThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(AndThenDeprecation)
fun <A, B, C> Kind<Kind<ForAndThen, A>, B>.andThen(arg1: Kind<Kind<ForAndThen, B>, C>): AndThen<A,
  C> = arrow.core.AndThen.category().run {
  this@andThen.andThen<A, B, C>(arg1) as arrow.core.AndThen<A, C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(AndThenDeprecation)
inline fun Companion.category(): AndThenCategory = category_singleton
