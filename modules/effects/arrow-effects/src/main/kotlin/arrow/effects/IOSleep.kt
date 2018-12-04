package arrow.effects

import arrow.core.Right
import arrow.effects.internal.ForwardCancelable
import arrow.effects.typeclasses.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Sleep for the given [duration] emitting a tick when that time span is over.
 *
 * {: data-executable='true'}
 * ```kotlin:ank
 *  import arrow.effects.*
 *  import arrow.effects.typeclasses.seconds
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   IO.sleep(3.seconds).flatMap {
 *     IO { println("Hello World") }
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * @param duration [Duration] to sleep.
 * @return [IO] which will emit tick after [duration]
 */
fun IO.Companion.sleep(duration: Duration): IO<Unit> = IO.async { conn, cb ->

  // Doing what IO.cancelable does
  val ref = ForwardCancelable()
  conn.push(ref.cancel())

  // Race condition test
  if (!conn.isCanceled()) {

    val scheduledFuture = scheduler.schedule({
      conn.pop()
      cb(Right(Unit))
    }, duration.nanoseconds, TimeUnit.NANOSECONDS)

    ref.complete(IO {
      scheduledFuture.cancel(false)
      Unit
    })

  } else ref.complete(IO.unit)
}

private val scheduler: ScheduledExecutorService by lazy {
  Executors.newScheduledThreadPool(2) { r ->
    Thread(r).apply {
      name = "arrow-effects-scheduler-$id"
      isDaemon = true
    }
  }
}
