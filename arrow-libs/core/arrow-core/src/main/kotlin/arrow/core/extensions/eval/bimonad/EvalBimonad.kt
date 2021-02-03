package arrow.core.extensions.eval.bimonad

import arrow.core.Eval.Companion
import arrow.core.extensions.EvalBimonad

/**
 * cached extension
 */
@PublishedApi()
internal val bimonad_singleton: EvalBimonad = object : arrow.core.extensions.EvalBimonad {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Bimonad typeclass is deprecated. Use concrete methods on Eval")
inline fun Companion.bimonad(): EvalBimonad = bimonad_singleton
