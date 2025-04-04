package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// Based on https://medium.com/@sam-cooper/custom-kotlin-coroutine-races-77161141b2ac

/**
 * Races the participants [tasks] in parallel on the [Dispatchers.Default],
 * until one of them returns a value which satisfies [condition].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 */
public suspend inline fun <A, B> raceUntil(
  crossinline condition: (A) -> Boolean,
  vararg tasks: suspend CoroutineScope.() -> A
): A = raceUntil(Dispatchers.Default, condition, *tasks)

/**
 * Races the participants [tasks] in parallel on the provided [ctx],
 * until one of them returns a value which satisfies [condition].
 * The winner of the race cancels the other participants.
 * Cancelling the operation cancels all participants.
 */
public suspend inline fun <A> raceUntil(
  ctx: CoroutineContext = EmptyCoroutineContext,
  crossinline condition: (A) -> Boolean,
  vararg tasks: suspend CoroutineScope.() -> A
): A {
  return coroutineScope {
    channelFlow {
      tasks.forEach { launch(ctx) { send(it()) } }
    }.first { condition(it) }
  }
}
