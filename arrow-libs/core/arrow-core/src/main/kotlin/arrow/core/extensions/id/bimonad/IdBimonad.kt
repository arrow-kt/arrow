package arrow.core.extensions.id.bimonad

import arrow.core.Id.Companion
import arrow.core.extensions.IdBimonad
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val bimonad_singleton: IdBimonad = object : arrow.core.extensions.IdBimonad {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bimonad(): IdBimonad = bimonad_singleton
