package arrow.core.extensions.setk.monoidK

import arrow.Kind
import arrow.core.ForSetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKMonoidK
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: SetKMonoidK = object : arrow.core.extensions.SetKMonoidK {}

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
    "Monoid.set<A>()",
    "arrow.typeclasses.Monoid",
    "arrow.core.set"
  ),
  DeprecationLevel.WARNING
)
fun <A> algebra(): Monoid<Kind<ForSetK, A>> = arrow.core.SetK
  .monoidK()
  .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForSetK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun Companion.monoidK(): SetKMonoidK = monoidK_singleton
