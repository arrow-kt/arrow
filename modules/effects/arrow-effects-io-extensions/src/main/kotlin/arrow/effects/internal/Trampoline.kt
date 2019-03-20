package arrow.effects.internal

import arrow.core.nonFatalOrThrow
import java.util.concurrent.ExecutorService

/**
 * Trampoline implementation, meant to be stored in a `ThreadLocal`.
 * See `TrampolineEC`.
 *
 * INTERNAL API.
 */
internal class Trampoline(val underlying: ExecutorService) {
  private var immediateQueue = Platform.ArrayStack<Runnable>()
  private var withinLoop = false

  fun startLoop(runnable: Runnable): Unit {
    withinLoop = true
    try {
      immediateLoop(runnable)
    } finally {
      withinLoop = false
    }
  }

  fun execute(runnable: Runnable): Unit {
    if (!withinLoop) {
      startLoop(runnable)
    } else {
      immediateQueue.push(runnable)
    }
  }

  private fun forkTheRest(): Unit {
    class ResumeRun(val head: Runnable, val rest: Platform.ArrayStack<Runnable>) : Runnable {
      override fun run(): Unit {
        immediateQueue.addAll(rest)
        immediateLoop(head)
      }
    }

    val head = immediateQueue.pop()
    if (head != null) {
      val rest = immediateQueue
      immediateQueue = Platform.ArrayStack<Runnable>()
      underlying.execute(ResumeRun(head, rest))
    }
  }

  private tailrec fun immediateLoop(task: Runnable): Unit {
    try {
      task.run()
    } catch (ex: Throwable) {
      forkTheRest()
      throw ex.nonFatalOrThrow()
    }

    val next = immediateQueue.pop()
    if (next != null) immediateLoop(next)
  }
}