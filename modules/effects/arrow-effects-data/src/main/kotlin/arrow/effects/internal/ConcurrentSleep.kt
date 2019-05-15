package arrow.effects.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.mapUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

@Suppress("FunctionName")
fun <F> Concurrent<F>.ConcurrentSleep(duration: Duration): Kind<F, Unit> = cancelable { cb ->
  val cancelRef = scheduler.schedule(ShiftTick(dispatchers().default(), cb), duration.amount, duration.timeUnit)
  delay { cancelRef.cancel(false).let(mapUnit) }
}

/**
 * [scheduler] is **only** for internal use for the [Concurrent.sleep] implementation.
 * This way we can guarantee nothing besides sleeping ever occurs here.
 */
internal val scheduler: ScheduledExecutorService by lazy {
  Executors.newScheduledThreadPool(2) { r ->
    Thread(r).apply {
      name = "arrow-effect-scheduler-$id"
      isDaemon = true
    }
  }
}

/**
 * [ShiftTick] is a small utility to [Concurrent.shift] work away from the [ScheduledExecutorService].
 * As mentioned in [scheduler] no work should ever happen there.
 * So after sleeping we need to shift away to not keep that thread occupied.
 */
internal class ShiftTick(
  private val ctx: CoroutineContext,
  private val cb: (Either<Throwable, Unit>) -> Unit
) : Runnable {
  override fun run() {
    suspend { Unit }.startCoroutine(Continuation(ctx) {
      it.fold({ unit -> cb(Right(unit)) }, { e -> cb(Left(e)) })
    })
  }
}

class TimeoutException(message: String) : Exception(message)
