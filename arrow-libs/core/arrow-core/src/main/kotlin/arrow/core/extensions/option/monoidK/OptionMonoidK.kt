package arrow.core.extensions.option.monoidK

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonoidK
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: OptionMonoidK = object : arrow.core.extensions.OptionMonoidK {}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "algebra()",
  "arrow.core.Option.algebra"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Monoid<Kind<ForOption, A>> = arrow.core.Option
   .monoidK()
   .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForOption, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monoidK(): OptionMonoidK = monoidK_singleton
