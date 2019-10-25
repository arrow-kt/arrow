package arrow.fx.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

internal fun <F> Concurrent<F, Throwable>.ConcurrentSleep(duration: Duration): Kind<F, Unit> = cancelable { cb ->
  val cancelRef = scheduler.schedule(ShiftTick(dispatchers().default(), cb), duration.amount, duration.timeUnit)
  later { cancelRef.cancel(false); Unit }
}

internal fun <F, E> Concurrent<F, E>.ConcurrentSleep(duration: Duration, fe: (Throwable) -> E): Kind<F, Unit> = cancelable { cb ->
  val cancelRef = scheduler.schedule(ShiftTick(dispatchers().default(), cb, fe), duration.amount, duration.timeUnit)
  later { cancelRef.cancel(false); Unit }
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
internal class ShiftTick<E>(
  private val ctx: CoroutineContext,
  private val cb: (Either<E, Unit>) -> Unit,
  private val fe: (Throwable) -> E
) : Runnable {

  override fun run() {
    suspend { Unit }.startCoroutine(Continuation(ctx) {
      it.fold({ unit -> cb(Right(unit)) }, { e -> cb(Left(fe(e))) })
    })
  }

  companion object {
    operator fun invoke(
      ctx: CoroutineContext,
      cb: (Either<Throwable, Unit>) -> Unit
    ) = ShiftTick(ctx, cb, ::identity)

    operator fun <E> invoke(
      ctx: CoroutineContext,
      cb: (Either<E, Unit>) -> Unit,
      fe: (Throwable) -> E
    ) = ShiftTick(ctx, cb, fe)
  }
}

class TimeoutException(override val message: String) : Exception()
