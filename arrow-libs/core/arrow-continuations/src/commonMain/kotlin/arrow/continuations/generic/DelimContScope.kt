package arrow.continuations.generic

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

/**
 * Implements delimited continuations with with no multi shot support (apart from shiftCPS which trivially supports it).
 *
 * For a version that simulates multishot (albeit with drawbacks) see [MultiShotDelimContScope].
 * For a version that allows nesting [reset] and calling parent scopes inside inner scopes see [NestedDelimContScope].
 *
 * The basic concept here is appending callbacks and polling for a result.
 * Every shift is evaluated until it either finishes (short-circuit) or suspends (called continuation). When it suspends its
 *  continuation is appended to a list waiting to be invoked with the final result of the block.
 * When running a function we jump back and forth between the main function and every function inside shift via their continuations.
 */
internal open class DelimContScope<R>(private val f: suspend RestrictedScope<R>.() -> R) : RestrictedScope<R> {

  /**
   * Variable used for polling the result after suspension happened.
   */
  private var resultVar: Any? = EMPTY_VALUE

  /**
   * Variable for the next shift block to (partially) run, if it is empty that usually means we are done.
   */
  private var nextShift: (suspend () -> R)? = null

  /**
   * "Callbacks"/partially evaluated shift blocks which now wait for the final result
   */
  // TODO This can be append only, but needs fast reversed access
  private val shiftFnContinuations = mutableListOf<Continuation<R>>()

  /**
   * Small wrapper that handles invoking the correct continuations and appending continuations from shift blocks
   */
  public data class SingleShotCont<A, R>(
    private val continuation: Continuation<A>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    public override suspend fun invoke(a: A): R = suspendCoroutineUninterceptedOrReturn { resumeShift ->
      shiftFnContinuations.add(resumeShift)
      continuation.resume(a)
      COROUTINE_SUSPENDED
    }
  }

  /**
   * Wrapper that handles invoking manually cps transformed continuations
   */
  public data class CPSCont<A, R>(
    private val runFunc: suspend DelimitedScope<R>.(A) -> R
  ) : DelimitedContinuation<A, R> {
    public override suspend fun invoke(a: A): R = DelimContScope<R> { runFunc(a) }.invoke()
  }

  /**
   * Captures the continuation and set [f] with the continuation to be executed next by the runloop.
   */
  public override suspend fun <A> shift(f: suspend RestrictedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutineUninterceptedOrReturn { continueMain ->
      val delCont = SingleShotCont(continueMain, shiftFnContinuations)
      require(nextShift == null)
      nextShift = suspend { this.f(delCont) }
      COROUTINE_SUSPENDED
    }

  /**
   * Same as [shift] except we never resume execution because we only continue in [c].
   */
  public suspend fun <A, B> shiftCPS(f: suspend (DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing =
    suspendCoroutineUninterceptedOrReturn {
      require(nextShift == null)
      nextShift = suspend { f(CPSCont(c)) }
      COROUTINE_SUSPENDED
    }

  /**
   * Unsafe if [f] calls [shift] on this scope! Use [NestedDelimContScope] instead if this is a problem.
   */
  fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A =
    DelimContScope(f).invoke()

  @Suppress("UNCHECKED_CAST")
  fun invoke(): R {
    f.startCoroutineUninterceptedOrReturn(
      this,
      Continuation(EmptyCoroutineContext) { result ->
        resultVar = result.getOrThrow()
      }
    ).let {
      if (it == COROUTINE_SUSPENDED) {
        // we have a call to shift so we must start execution the blocks there
        while (true) {
          if (resultVar === EMPTY_VALUE) {
            val nextShiftFn = requireNotNull(nextShift) { "No further work to do but also no result!" }
            nextShift = null
            nextShiftFn.startCoroutineUninterceptedOrReturn(
              Continuation(EmptyCoroutineContext) { result ->
                resultVar = result.getOrThrow()
              }
            ).let { nextRes ->
              // If we suspended here we can just continue to loop because we should now have a new function to run
              // If we did not suspend we short-circuited and are thus done with looping
              if (nextRes != COROUTINE_SUSPENDED) resultVar = nextRes as R
            }
            // Break out of the infinite loop if we have a result
          } else return@let
        }
      }
      // we can return directly if we never suspended/called shift
      else return@invoke it as R
    }
    require(resultVar !== EMPTY_VALUE)
    // We need to finish the partially evaluated shift blocks by passing them our result.
    // This will update the result via the continuations that now finish up
    for (c in shiftFnContinuations.asReversed()) c.resume(resultVar as R)
    // Return the final result
    return resultVar as R
  }
}

@Suppress("ClassName")
internal object EMPTY_VALUE {
  @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
  public inline fun <T> unbox(value: Any?): T =
    if (value === this) null as T else value as T
}
