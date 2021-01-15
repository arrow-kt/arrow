package arrow.core.extensions.set.monoidK

import arrow.Kind
import arrow.core.ForSetK
import arrow.core.extensions.SetKMonoidK
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

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
  "arrow.core.extensions.set.monoidK.Set.algebra"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Monoid<Kind<ForSetK, A>> = arrow.core.extensions.set.monoidK.Set
   .monoidK()
   .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForSetK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: SetKMonoidK = object : arrow.core.extensions.SetKMonoidK {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monoidK(): SetKMonoidK = monoidK_singleton}
