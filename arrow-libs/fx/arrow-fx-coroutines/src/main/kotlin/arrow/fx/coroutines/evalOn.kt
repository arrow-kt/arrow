package arrow.fx.coroutines

import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@Deprecated("Use withContext", replaceWith = ReplaceWith("withContext(ctx, block)", "kotlinx.coroutines.withContext"))
suspend fun <T> (suspend () -> T).evalOn(ctx: CoroutineContext): T =
  evalOn(ctx, this)

/**
 * Executes a task on [context] and comes back to the original [CoroutineContext].
 *
 * State of [context] and previous [CoroutineContext] is merged
 */
@Deprecated("Use withContext", replaceWith = ReplaceWith("withContext(context, block)", "kotlinx.coroutines.withContext"))
suspend fun <T> evalOn(
  context: CoroutineContext,
  block: suspend () -> T
): T = withContext(context) { block.invoke() }
