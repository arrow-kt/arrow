package arrow.core.extensions.eval.bimonad

import arrow.core.Eval.Companion
import arrow.core.extensions.EvalBimonad
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val bimonad_singleton: EvalBimonad = object : arrow.core.extensions.EvalBimonad {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bimonad(): EvalBimonad = bimonad_singleton
