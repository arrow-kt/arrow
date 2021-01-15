package arrow.core.extensions.set.monoidal

import arrow.core.extensions.SetKMonoidal
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Set
import kotlin.jvm.JvmName

@JvmName("identity")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "identity()",
  "arrow.core.extensions.set.monoidal.Set.identity"
  ),
  DeprecationLevel.WARNING
)
fun <A> identity(): Set<A> = arrow.core.extensions.set.monoidal.Set
   .monoidal()
   .identity<A>() as kotlin.collections.Set<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: SetKMonoidal = object : arrow.core.extensions.SetKMonoidal {}

object Set {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monoidal(): SetKMonoidal = monoidal_singleton}
