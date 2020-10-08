package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.resume

suspend fun <T> (suspend () -> T).evalOn(ctx: CoroutineContext): T =
  evalOn(ctx, this)

/**
 * Executes a task on [context] and comes back to the original [CoroutineContext].
 *
 * State of [context] and previous [CoroutineContext] is merged
 */
suspend fun <T> evalOn(
  context: CoroutineContext,
  block: suspend () -> T
): T = suspendCoroutineUninterceptedOrReturn sc@{ uCont ->

  if ((uCont.context[SuspendConnection] ?: SuspendConnection.uncancellable).isCancelled()) return@sc COROUTINE_SUSPENDED

  val oldContext = uCont.context
  val newContext = oldContext + context

  // FAST PATH #1 -- new context is the same as the old one
  // FAST PATH #2 -- same ContinuationInterceptor, something else changed
  // There are changes in the context, so this thread needs to be updated
  if (newContext === oldContext || newContext[ContinuationInterceptor] === oldContext[ContinuationInterceptor]) {
    val coroutine = NonDispatchableCoroutine(newContext, uCont)

    val x = block.startCoroutineUninterceptedOrReturn(coroutine)
    return@sc (if (x != COROUTINE_SUSPENDED) x else coroutine.getResult())
  }

  val coroutine = DispatchableCoroutine(newContext, uCont)
  block.createCoroutineUnintercepted(coroutine).intercepted().resume(Unit)
  return@sc coroutine.getResult()
}

internal interface SafeCoroutine {
  fun getResult(): Any?
}

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

private class NonDispatchableCoroutine<in T>(
  ctx: CoroutineContext,
  private val uCont: Continuation<T>
) : Continuation<T>, CoroutineStackFrame, SafeCoroutine {

  override val context: CoroutineContext = ctx

  private val _decision = atomic<Any?>(UNDECIDED)

  override fun resumeWith(result: Result<T>) {
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> {
          val r: Any? = result.fold({ it }) { t -> uCont.resumeWithException(t) }
          if (this._decision.compareAndSet(UNDECIDED, r)) return
        }
        else -> { // If not `UNDECIDED` then we need to pass result to `uCont`
          uCont.resumeWith(result)
          return
        }
      }
    }
  }

  override fun getResult(): Any? =
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, SUSPENDED)) return COROUTINE_SUSPENDED
        else -> return decision
      }
    }

  override val callerFrame: CoroutineStackFrame?
    get() = this

  override fun getStackTraceElement(): StackTraceElement? =
    null
}

private class DispatchableCoroutine<in T>(
  ctx: CoroutineContext,
  private val uCont: Continuation<T>
) : Continuation<T>, CoroutineStackFrame, SafeCoroutine {

  override val context: CoroutineContext = ctx

  override fun resumeWith(result: Result<T>) {
    uCont.intercepted().resumeWith(result)
  }

  override fun getResult(): Any? = COROUTINE_SUSPENDED

  override val callerFrame: CoroutineStackFrame?
    get() = this

  override fun getStackTraceElement(): StackTraceElement? =
    null
}

internal interface CoroutineStackFrame : kotlin.coroutines.jvm.internal.CoroutineStackFrame {
  override val callerFrame: CoroutineStackFrame?
  override fun getStackTraceElement(): StackTraceElement?
}
