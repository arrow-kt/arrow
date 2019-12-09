package arrow.fx.typeclasses

import arrow.Kind
import arrow.fx.CancelToken
import arrow.higherkind

/**
 * [Fiber] represents the pure result of an [Async] data type
 * being started concurrently and that can be either joined or canceled.
 *
 * You can think of fibers as being lightweight threads, a Fiber being a
 * concurrency primitive for doing cooperative multi-tasking.
 */
@higherkind interface Fiber<F, out A> : FiberOf<F, A> {

  /**
   * Returns a new task that will await for the completion of the
   * underlying [Fiber], (asynchronously) blocking the current run-loop
   * until that result is available.
   */
  fun join(): Kind<F, A>

  /**
   * Triggers the cancellation of the [Fiber].
   *
   * @returns a task that trigger the cancellation upon evaluation.
   */
  fun cancel(): CancelToken<F>

  operator fun component1(): Kind<F, A> = join()
  operator fun component2(): CancelToken<F> = cancel()

  companion object {

    /**
     * [Fiber] constructor.
     *
     * @param join task that will trigger the cancellation.
     * @param cancel task that will await for the completion of the underlying Fiber.
     */
    operator fun <F, A> invoke(join: Kind<F, A>, cancel: CancelToken<F>): Fiber<F, A> = object : Fiber<F, A> {
      override fun join(): Kind<F, A> = join
      override fun cancel(): CancelToken<F> = cancel
      override fun toString(): String = "Fiber(join= ${join()}, cancel= ${cancel()})"
    }
  }
}
