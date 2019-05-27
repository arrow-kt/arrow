package arrow.effects.typeclasses

import arrow.Kind
import arrow.effects.internal.ConcurrentSleep

/**
 * [Timer] allows to [sleep] for a [Duration] in [F].
 * This behaviour can be derived from [Concurrent], and can be used to implement backing off retries etc.
 *
 * Since sleeping is done by [Timer] it allows for easy modification in testing by providing a no-op [TestTimer]
 */
interface Timer<F> {
  /**
   *  Sleeps for a given [duration] without blocking a thread.
   *
   * ```kotlin:ank:playground
   * import arrow.*
   * import arrow.effects.*
   * import arrow.effects.typeclasses.*
   * import arrow.effects.extensions.io.concurrent.concurrent
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Concurrent<F>.delayHelloWorld(): Kind<F, Unit> =
   *     timer().sleep(3.seconds).flatMap {
   *       delay { println("Hello World!") }
   *     }
   *   //sampleEnd
   *   IO.concurrent().delayHelloWorld()
   *     .fix().unsafeRunSync()
   * }
   * ```
   **/
  fun sleep(duration: Duration): Kind<F, Unit>

  companion object {
    operator fun <F> invoke(CF: Concurrent<F>): Timer<F> = object : Timer<F> {
      override fun sleep(duration: Duration): Kind<F, Unit> = CF.ConcurrentSleep(duration)
    }
  }
}
