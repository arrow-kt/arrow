package arrow.core

import arrow.Kind
import arrow.typeclasses.suspended.BindSyntax
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.resumeWithException

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

@Suppress("UNCHECKED_CAST")
internal abstract class SuspendMonadContinuation<F, A>(
  private val parent: Continuation<Kind<F, A>>
) : Continuation<Kind<F, A>>, BindSyntax<F> {

  abstract fun ShortCircuit.recover(): Kind<F, A>

  /**
   * State is either
   *  0 - UNDECIDED
   *  1 - SUSPENDED
   *  Any? (3) `resumeWith` always stores it upon UNDECIDED, and `getResult` can atomically get it.
   */
  private val _decision = atomic<Any>(UNDECIDED)

  override val context: CoroutineContext = EmptyCoroutineContext

  override fun resumeWith(result: Result<Kind<F, A>>) {
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> {
          val r: Kind<F, A>? = when {
            result.isFailure -> {
              val e = result.exceptionOrNull()
              if (e is ShortCircuit) e.recover() else null
            }
            result.isSuccess -> result.getOrNull()
            else -> throw ArrowCoreInternalException
          }

          when {
            r == null -> {
              parent.resumeWithException(result.exceptionOrNull()!!)
              return
            }
            _decision.compareAndSet(UNDECIDED, r) -> return
            else -> Unit // loop again
          }
        }
        else -> { // If not `UNDECIDED` then we need to pass result to `parent`
          val res: Result<Kind<F, A>> = result.fold(
            { Result.success(it) },
            { t ->
              if (t is ShortCircuit) Result.success(t.recover())
              else Result.failure(t)
            }
          )
          parent.resumeWith(res)
          return
        }
      }
    }
  }

  @PublishedApi // return the result
  internal fun getResult(): Any? =
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, SUSPENDED)) return COROUTINE_SUSPENDED
        else -> return decision
      }
    }

  fun startCoroutineUninterceptedOrReturn(f: suspend SuspendMonadContinuation<F, A>.() -> Kind<F, A>): Any? =
    try {
      f.startCoroutineUninterceptedOrReturn(this, this)?.let {
        if (it == COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit) e.recover()
      else throw e
    }
}
