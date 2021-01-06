package arrow.core.extensions.listk.monoidal

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKMonoidal
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: ListKMonoidal = object : arrow.core.extensions.ListKMonoidal {}

@JvmName("identity")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("emptyList<A>()"))
fun <A> identity(): ListK<A> = arrow.core.ListK
   .monoidal()
   .identity<A>() as arrow.core.ListK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Monoidal typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.monoidal(): ListKMonoidal = monoidal_singleton
