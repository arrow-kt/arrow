package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Delimited control version which allows `reset { ... reset { ... } }` to function correctly.
 *
 * [DelimContScope] fails at this if you call shift on the parent scope inside the inner reset.
 * For a version that allows simulated multishot (with drawbacks) see [MultiShotDelimContScope].
 *
 * The implementation is basically the same as [DelimContScope] except that we now need to respect calls through different runloops.
 *
 * Comments in here only describe what differs from [DelimContScope] and why it differs.
 *  Make sure you understand [DelimContScope] before reading this.
 *  (When this is somewhat more stable we can copy comments over until then it would be annoying to maintain)
 *
 * > It would be possible to collapse to one runloop however that comes with drawbacks:
 * > - no typesafety because each nested block may result in different types
 * > - slightly more complex implementation because each nest must when it finishes know what continuations to resume
 */
open class NestedDelimContScope<R>(val f: suspend DelimitedScope<R>.() -> R) : DelimitedScope<R> {

  private val resultVar = atomic<R?>(null)

  // We now need a way for nested scopes to access this variable and the atomic plugin prevents direct access
  internal fun getResult(): R? = resultVar.value
  internal fun setResult(r: R): Unit {
    resultVar.value = r
  }

  // Short hand for AtomicRef.loop which the atomic plugin prevents from use from outside of this class afaik
  internal inline fun loopNoResult(f: () -> Unit): Unit {
    while (true) {
      if (getResult() == null) f()
      else return
    }
  }

  internal val nextShift = atomic<(suspend () -> R)?>(null)

  // TODO This can be append only and needs fast reversed access
  internal val shiftFnContinuations = mutableListOf<Continuation<R>>()

  data class SingleShotCont<A, R>(
    private val continuation: Continuation<A>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = suspendCoroutine { resumeShift ->
      shiftFnContinuations.add(resumeShift)
      continuation.resume(a)
    }
  }

  data class CPSCont<A, R>(
    private val runFunc: suspend DelimitedScope<R>.(A) -> R
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = DelimContScope<R> { runFunc(a) }.invoke()
  }

  override suspend fun <A> shift(func: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutine { continueMain ->
      val delCont = SingleShotCont(continueMain, shiftFnContinuations)
      assert(nextShift.compareAndSet(null, suspend { this.func(delCont) }))
    }

  override suspend fun <A, B> shiftCPS(func: suspend (DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing =
    suspendCoroutine {
      assert(nextShift.compareAndSet(null, suspend { func(CPSCont(c)) }))
    }

  // Here we create a new scope and pass this scope as a parent
  override suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A =
    ChildDelimContScope(this, f)
      .invokeNested()

  // helper to execute one single shift function.
  // hdlMissingWork is used to handle the case where we suspended but there are no shift functions to execute
  //  in our scope. This means one of two things: In the top level scope this is an error, in a child this means that a parent
  //  now has a shift function to execute and we need to yield to the parent.
  internal inline fun step(hdlMisingWork: () -> Unit): Unit {
    val nextShiftFn = nextShift.getAndSet(null)
      ?: return hdlMisingWork()
    nextShiftFn.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { result ->
      resultVar.value = result.getOrThrow()
    }).let {
      if (it != COROUTINE_SUSPENDED) resultVar.value = it as R
    }
  }

  // This is basically the same as as in DelimContScope but split across a few helpers to ease reuse in nested scopes
  open fun invoke(): R {
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) { result ->
      resultVar.value = result.getOrThrow()
    }).let {
      if (it == COROUTINE_SUSPENDED) {
        loopNoResult {
          // At the top level not having a shift function to continue and not having a result is an error
          step { throw IllegalStateException("Suspended parent scope, but found no further work") }
        }
      } else return@invoke it as R
    }

    assert(resultVar.value != null)
    for (c in shiftFnContinuations.asReversed()) c.resume(resultVar.value!!)
    return resultVar.value!!
  }

  // on the top level this checks if we have work to do and returns this if that is the case
  // on nested scopes this searches all parents as well
  open fun getActiveParent(): NestedDelimContScope<*>? = this.takeIf { nextShift.value != null }

  companion object {
    fun <R> reset(f: suspend DelimitedScope<R>.() -> R): R = NestedDelimContScope(f).invoke()
  }
}

class ChildDelimContScope<R>(
  val parent: NestedDelimContScope<*>,
  f: suspend DelimitedScope<R>.() -> R
) : NestedDelimContScope<R>(f) {
  // search all parents to find the one that has work to do
  override fun getActiveParent(): NestedDelimContScope<*>? =
    super.getActiveParent() ?: parent.getActiveParent()

  // Instead of yielding to a parents runloop (which complicates reentering our own runloop a bit) we instead perform our parents
  //  work from the child scope which is no problem because we just need to run its queued suspend functions and poll for a result
  //  after each invocation of step we also check if the parent finished (which means short-circuit) or the parent suspended which
  //  which means work has been queued again (could be anywhere which is why we search again in the loop).
  //  If we are the ones that need to resume we break the loop and invokeNested will resume work
  private suspend fun performParentWorkIfNeeded(): Unit {
    while (true) {
      parent.getActiveParent()?.let { scope ->
        // No need to do anything in steps cb because we handle this case from down here
        scope.step {}
        // parent short circuited
        if (scope.getResult() != null) suspendCoroutine<Nothing> {}
      } ?: break
    }
  }

  suspend fun invokeNested(): R {
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) { result ->
      setResult(result.getOrThrow())
    }).let {
      if (it == COROUTINE_SUSPENDED) {
        loopNoResult {
          // if we run out of work we need to check and run work for one of our parent scopes
          step { performParentWorkIfNeeded() }
        }
      } else return@invokeNested it as R
    }

    assert(getResult() != null)
    for (c in shiftFnContinuations.asReversed()) c.resume(getResult()!!)
    return getResult()!!
  }

  // This is now unsupported because it does not allow suspension which is key in yielding to the parent if the parent short-circuited
  // TODO if invoke ever becomes suspended invokeNested should be removed
  override fun invoke(): R {
    println("""
      Using invoke() for child scope.
      This will break on nested calls to reset and using shift from different scopes inside those.
      Use invokeNested instead.
      """)
    return super.invoke()
  }
}
