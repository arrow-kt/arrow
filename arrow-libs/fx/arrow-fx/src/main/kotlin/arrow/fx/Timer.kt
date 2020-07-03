package arrow.fx

import arrow.Kind
import arrow.fx.internal.ConcurrentSleep
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Duration

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
   * import arrow.fx.*
   * import arrow.fx.typeclasses.*
   * import arrow.fx.extensions.io.concurrent.concurrent
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   fun <F> Concurrent<F>.delayHelloWorld(): Kind<F, Unit> =
   *     Timer(this).sleep(3.seconds).flatMap {
   *       effect { println("Hello World!") }
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
