package arrow.effects.suspended.fx

import arrow.effects.internal.Platform
import java.util.concurrent.Executor
import kotlin.concurrent.getOrSet
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

object TrampolineRunLoop {

  @PublishedApi
  internal val maxStackSize = 127

  suspend inline operator fun <A> invoke(noinline f: suspend () -> A): A {
    val currentIterations = iterations.get()
    return if (currentIterations <= maxStackSize) {
      iterations.set(currentIterations + 1)
      f()
    } else {
      //println("reached iterations: $currentIterations, trampolining...")
      iterations.set(0)
      suspendCoroutine { cont ->
        executor
          .getOrSet { TrampolineExecutor(Executor { it.run() }) }
          .execute(Runnable {
            f.startCoroutine(Continuation(EmptyCoroutineContext) {
              cont.resumeWith(it)
            })
          })
      }
    }
  }

  @PublishedApi
  internal val iterations: ThreadLocal<Int> = ThreadLocal.withInitial { 0 }

  @PublishedApi
  internal val executor: ThreadLocal<TrampolineExecutor> = ThreadLocal()

  @PublishedApi
  internal class TrampolineExecutor(val underlying: Executor) {
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
          immediateQueue.pushAll(rest)
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
        //ex.nonFatalOrThrow()
      }

      val next = immediateQueue.pop()
      if (next != null) {
        immediateLoop(next)
      }
    }
  }



}