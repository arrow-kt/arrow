package arrow.core.extensions.setk.monoidal

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKMonoidal
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: SetKMonoidal = object : arrow.core.extensions.SetKMonoidal {}

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
  "arrow.core.SetK.identity"
  ),
  DeprecationLevel.WARNING
)
fun <A> identity(): SetK<A> = arrow.core.SetK
   .monoidal()
   .identity<A>() as arrow.core.SetK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monoidal(): SetKMonoidal = monoidal_singleton
