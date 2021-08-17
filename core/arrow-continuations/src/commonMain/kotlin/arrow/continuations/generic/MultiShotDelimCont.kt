package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * (Simulated) Multishot capable delimited control scope
 *
 * This has several drawbacks:
 * - f will rerun completely on multishot and only the results of [shift] are cached so any sideeffects outside of
 *   [shift] will rerun!
 * - This accumulates all results of [shift] (every argument passed when invoking the continuation) so on long running computations
 *   this may keep quite a bit of memory
 * - If the pure part before a multishot is expensive the multishot itself will have to rerun that, which makes it somewhat slow
 * - This is terribly hard to implement properly with nested scopes (which this one does not support)
 *
 * As per usual understanding of [DelimContScope] is required as I will only be commenting differences for now.
 */
internal open class MultiShotDelimContScope<R>(val f: suspend RestrictedScope<R>.() -> R) : RestrictedScope<R> {

  // TODO Since runs blocking these don't need to be atomic
  private val resultVar = atomic<R?>(null)
  private val nextShift = atomic<(suspend RestrictedScope<R>.() -> R)?>(null)

  // TODO This can be append only and needs fast reversed access
  private val shiftFnContinuations = mutableListOf<Continuation<R>>()

  /**
   * Keep the arguments passed to [DelimitedContinuation.invoke] to be able to replay the scope if necessary
   */
  // TODO This can be append only and needs fast random access and slicing
  internal open val stack = mutableListOf<Any?>()

  /**
   * Our continuation now includes the function [f] to rerun on multishot, the current live (single-shot) continuation,
   *  the current stack and the offset from that stack when this is created which is used to know when to resume normal
   *  execution again on a replay.
   */
  public class MultiShotCont<A, R>(
    liveContinuation: Continuation<A>,
    private val f: suspend RestrictedScope<R>.() -> R,
    private val stack: MutableList<Any?>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    // To make sure the continuation is only invoked once we put it in a nullable atomic and only access it through getAndSet
    private val liveContinuation = atomic<Continuation<A>?>(liveContinuation)
    private val stackOffset = stack.size

    public override suspend fun invoke(a: A): R =
      when (val cont = liveContinuation.getAndSet(null)) {
        // On multishot we replay with a prefilled stack from start to the point at which this object was created
        //  (when the shift block this runs in was first called)
        null -> PrefilledDelimContScope((stack.subList(0, stackOffset).toList() + a).toMutableList(), f).invoke()
        // on the first pass we operate like a normal delimited scope but we also save the argument to the stack before resuming
        else -> suspendCoroutine { resumeShift ->
          shiftFnContinuations.add(resumeShift)
          stack.add(a)
          cont.resume(a)
        }
      }
  }

  public data class CPSCont<A, R>(
    private val runFunc: suspend DelimitedScope<R>.(A) -> R
  ) : DelimitedContinuation<A, R> {
    public override suspend fun invoke(a: A): R = DelimContScope<R> { runFunc(a) }.invoke()
  }

  public override suspend fun <A> shift(func: suspend RestrictedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutineUninterceptedOrReturn { continueMain ->
      val c = MultiShotCont(continueMain, f, stack, shiftFnContinuations)
      val s: suspend RestrictedScope<R>.() -> R = { this.func(c) }
      require(nextShift.compareAndSet(null, s))
      COROUTINE_SUSPENDED
    }

  public suspend fun <A, B> shiftCPS(func: suspend DelimitedScope<R>.(DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing =
    suspendCoroutine {
      val s: suspend DelimitedScope<R>.() -> R = { func(CPSCont(c)) }
      require(nextShift.compareAndSet(null, s))
    }

  // This assumes RestrictSuspension or at least assumes the user to never reference the parent scope in f.
  public suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A =
    MultiShotDelimContScope(f).invoke()

  fun invoke(): R {
    f.startCoroutineUninterceptedOrReturn(
      this,
      Continuation(EmptyCoroutineContext) { result ->
        resultVar.value = result.getOrThrow()
      }
    ).let {
      if (it == COROUTINE_SUSPENDED) {
        resultVar.loop { mRes ->
          if (mRes == null) {
            val nextShiftFn = nextShift.getAndSet(null)
              ?: throw IllegalStateException("No further work to do but also no result!")
            nextShiftFn.startCoroutineUninterceptedOrReturn(
              this,
              Continuation(EmptyCoroutineContext) { result ->
                resultVar.value = result.getOrThrow()
              }
            ).let {
              if (it != COROUTINE_SUSPENDED) resultVar.value = it as R
            }
          } else return@let
        }
      } else return@invoke it as R
    }
    require(resultVar.value != null)
    for (c in shiftFnContinuations.asReversed()) c.resume(resultVar.value!!)
    return resultVar.value!!
  }

  public companion object {
    internal fun <R> reset(f: suspend RestrictedScope<R>.() -> R): R = MultiShotDelimContScope(f).invoke()
  }
}

private class PrefilledDelimContScope<R>(
  override val stack: MutableList<Any?>,
  f: suspend RestrictedScope<R>.() -> R
) : MultiShotDelimContScope<R>(f) {
  var depth = 0

  // Here we first check if we still have values in our local stack and if so we use those first
  //  if not we delegate to the normal delimited control implementation
  public override suspend fun <A> shift(func: suspend RestrictedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    if (stack.size > depth) stack[depth++] as A
    else {
      @Suppress("ILLEGAL_RESTRICTED_SUSPENDING_FUNCTION_CALL")
      super.shift(func).also { depth++ }
    }
}
