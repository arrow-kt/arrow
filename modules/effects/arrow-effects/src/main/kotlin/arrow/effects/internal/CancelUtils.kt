package arrow.effects.internal

import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IOFrame
import arrow.effects.fix

/**
 * utilities for dealing with cancelable thunks.
 */
internal object CancelUtils {

  /**
   * Given a list of cancel tokens, cancels all, delaying all exceptions until all references are canceled.
   */
  fun cancelAll(vararg cancelables: CancelToken<ForIO>): CancelToken<ForIO> =
    if (cancelables.isEmpty()) {
      IO.unit
    } else IO.defer {
      cancelAll(cancelables.iterator())
    }

  fun cancelAll(cursor: Iterator<CancelToken<ForIO>>): CancelToken<ForIO> =
    if (!cursor.hasNext()) {
      IO.unit
    } else IO.defer {
      val frame = CancelAllFrame(cursor)
      frame.loop()
    }

  /**
   * Optimization for cancelAll
   */
  private class CancelAllFrame(val cursor: Iterator<CancelToken<ForIO>>) : IOFrame<Unit, IO<Unit>> {

    private var errors = listOf<Throwable>()

    fun loop(): CancelToken<ForIO> =
      if (cursor.hasNext()) {
        cursor.next().fix().flatMap(this)
      } else {
        when (errors) {
          listOf<Throwable>() -> IO.unit
          else -> // first :: rest
            IO.raiseError(ErrorUtils.composeErrors(errors.first(), errors.drop(1)))
        }
      }

    override operator fun invoke(a: Unit): IO<Unit> = loop().fix()

    override fun recover(e: Throwable): IO<Unit> {
      errors += e
      return loop().fix()
    }
  }
}
