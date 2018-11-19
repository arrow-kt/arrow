package arrow.effects.internal

import arrow.effects.IO

object IOCancel {

  /** Implementation for `IO.uncancelable`. */
  fun <A> uncancelable(fa: IO<A>): IO<A> = IO.ContextSwitch(fa, makeUncancelable, disableUncancelable())

  /** Internal reusable reference. */
  private val makeUncancelable: (IOConnection) -> IOConnection = { IOConnection.uncancelable }
  private fun <A> disableUncancelable(): (A, Throwable?, IOConnection, IOConnection) -> IOConnection =
    { _, _, old, _ -> old }
}
