package arrow.effects.typeclasses

import arrow.Kind

/**
 * [Fiber] represents the pure result of an [Async] data type
 * being started concurrently and that can be either joined or canceled.
 *
 * You can think of fibers as being lightweight threads, a fiber being a
 * concurrency primitive for doing cooperative multi-tasking.
 */
interface Fiber<F, A> {

  /**
   * Returns a new task that will await for the completion of the
   * underlying fiber, (asynchronously) blocking the current run-loop
   * until that result is available.
   */
  val join: Kind<F, A>

  /**
   * Triggers the cancellation of the fiber.
   *
   * @returns a task that trigger the cancellation upon evaluation.
   */
  val cancel: Kind<F, Unit>

  operator fun component1(): Kind<F, A> = join
  operator fun component2(): Kind<F, Unit> = cancel

  companion object {

    /**
     * [Fiber] constructor.
     *
     * @param join task that will trigger the cancellation.
     * @param cancel task that will await for the completion of the underlying fiber.
     */
    operator fun <F, A> invoke(join: Kind<F, A>, cancel: Kind<F, Unit>): Fiber<F, A> = object : Fiber<F, A> {
      override val cancel: Kind<F, Unit> = cancel
      override val join: Kind<F, A> = join
      override fun toString(): String = "Fiber(join= $join, cancel= $cancel)"
    }
  }

}
