package arrow.core.extensions.nonemptylist.bimonad

import arrow.core.NonEmptyList.Companion
import arrow.core.extensions.NonEmptyListBimonad
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val bimonad_singleton: NonEmptyListBimonad = object :
    arrow.core.extensions.NonEmptyListBimonad {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bimonad(): NonEmptyListBimonad = bimonad_singleton
