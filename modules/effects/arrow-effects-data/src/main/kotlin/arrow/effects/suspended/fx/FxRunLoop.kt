package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.core.*
import arrow.effects.*
import arrow.effects.IORunLoop.startCancelable
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx.FxRunLoop.startCancelable
import arrow.effects.typeclasses.suspendMapUnit
import kotlin.coroutines.*
import kotlin.coroutines.Continuation

/**
 * This is the internal API for all methods that run the effect,
 * this includes all unsafe/safe run methods, [Fx.not], fibers and races.
 */
@Suppress("UNCHECKED_CAST")
@PublishedApi
internal object FxRunLoop {

  /** Internal API for [Fx.not] */
  suspend operator fun <A> invoke(source: FxOf<A>): A = suspendCoroutine { cont ->
    FxRunLoop.start(source) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  fun <A> start(source: FxOf<A>, ctx: CoroutineContext = EmptyCoroutineContext, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, FxNonCancelable, ctx, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  fun <A> startCancelable(fa: FxOf<A>,
                          token: KindConnection<ForFx>,
                          ctx: CoroutineContext = EmptyCoroutineContext,
                          cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(fa, token, ctx, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  /**
   * This is the **only** main entry point to running an [Fx] value.
   *
   * @param currToken [FxConnection] is an important detail because misuse can result in hard to debug code.
   * When started by [start] or [startCancelable] it's decided whether the loop will be cancelable or not.
   * Once a job is started uncancelable it can never become cancelable again,
   * only a cancelable job can become uncancelable temporary or permanently behind a certain point.
   * This is done using [Fx.ConnectionSwitch].
   *
   * @param ctxRef [CoroutineContext] that is visible throughout the suspended program `Fx { coroutineContext }`.
   * This is very important to keep compatibility with kotlins vision of [CoroutineContext] and [suspend].
   * [this](https://github.com/Kotlin/kotlin-coroutines-examples/blob/master/examples/context/auth-example.kt) has to work across any async boundary.
   *
   * @param cb the callback that will be called with the result as [Either].
   * @param rcbRef [AsyncBoundary] helper class instance that is shared across async boundaries
   * @param bFirstRef first [Fx.FlatMap] on the stack to restore to the state
   * @param bRestRef remaining [Fx.FlatMap] stack to restore to the state
   */
  @Suppress("CollapsibleIfStatements", "ReturnCount")
  private fun loop(fa: FxOf<Any?>,
                   currToken: FxConnection,
                   ctxRef: CoroutineContext,
                   cb: (Either<Throwable, Any?>) -> Unit,
                   rcbRef: AsyncBoundary?,
                   bFirstRef: ((Any?) -> Fx<Any?>)?,
                   bRestRef: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): Unit {

    //Once a loop is started it context doesn't change. You can only modify the context through `startCoroutine`.
    val ctx: CoroutineContext = ctxRef
    var conn: KindConnection<ForFx> = currToken
    var source: Fx<Any?>? = fa as Fx<Any?>
    var asyncBoundary: AsyncBoundary? = rcbRef
    var bFirst: ((Any?) -> Fx<Any?>)? = bFirstRef
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = bRestRef
    var hasResult = false
    var result: Any? = null

    while (true) {
      if (conn.isCanceled()) {
        cb(Either.Left(OnCancel.CancellationException))
        return
      }
      val tag = source?.tag ?: UnknownTag
      when (tag) {
        RaiseErrorTag -> {
          val errorHandler: FxFrame<Any?, Fx<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)
          when (errorHandler) {
            null -> {
              cb(Either.Left((source as Fx.RaiseError<Any?>).error))
              return
            }
            else -> {
              val error = (source as Fx.RaiseError<Any?>).error
              source = executeSafe { errorHandler.recover(error) }
              bFirst = null
            }
          }
        }
        ContinueOnTag -> {
          val localCurrent = (source as Fx.ContinueOn<Any?>)
          val nextCC = ctx + localCurrent.ctx
          val next = localCurrent.source

          source = Fx.FlatMap(next, { a ->    //ContinueOn is the current op followed by an async jump that updates the context.
            Fx.Async<Any?>(nextCC) { _, cb ->
              cb(Right(a))
            }
          }, 0)
        }
        PureTag -> {
          result = (source as Fx.Pure<Any?>).value
          hasResult = true
        }
        SingleTag -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }
          //Run the suspend function in the async boundary and return
          asyncBoundary.start(source as Fx.Single<Any?>, ctx, bFirst, bRest)
          return
        }
        MapTag -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = Platform.ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = source as ((Any?) -> Fx<Any?>)?
          source = (source as Fx.Map<Any?, Any?>).source.fix()
        }
        FlatMapTag -> {
          if (bFirst != null) {
            if (bRest == null) bRest = Platform.ArrayStack()
            bRest.push(bFirst)
          }
          source as Fx.FlatMap<Any?, Any?>
          bFirst = source.fb as ((Any?) -> Fx<Any?>)?
          source = source.source.fix()
        }
        AsyncTag -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }
          // Return case for Async operations
          asyncBoundary.start(source as Fx.Async<Any?>, ctx + source.ctx, bFirst, bRest)
          return
        }
        ConnectionSwitchTag -> {
          source as Fx.ConnectionSwitch<Any?>
          val next = source.source
          val modify = source.modify
          val restore = source.restore

          val old = conn
          conn = modify(old)
          source = next as? Fx<Any?>

          if (conn != old) {
            asyncBoundary?.contextSwitch(conn)
            if (restore != null) {
              source = Fx.FlatMap(next, FxRunLoop.RestoreContext(old, restore), 0)
            }
          }
        }
        UnknownTag -> source = Fx.RaiseError(NullPointerException("Looping on null Fx")) //Improve message
      }

      if (hasResult) {
        val nextBind = popNextBind(bFirst, bRest)

        if (nextBind == null) {
          cb(Either.Right(result))
          return
        } else {
          source = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    }
  }

  @Suppress("ReturnCount")
  fun findErrorHandlerInCallStack(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): FxFrame<Any?, Fx<Any?>>? {
    if (bFirst != null && bFirst is FxFrame) {
      return bFirst
    } else if (bRest == null) {
      return null
    }

    var result: FxFrame<Any?, Fx<Any?>>? = null
    var cursor: ((Any?) -> Fx<Any?>)? = bFirst

    @Suppress("LoopWithTooManyJumpStatements")
    do {
      if (cursor != null && cursor is FxFrame) {
        result = cursor
        break
      } else {
        cursor = if (bRest.isNotEmpty()) {
          bRest.pop()
        } else {
          break
        }
      }
    } while (true)
    return result
  }

  private inline fun executeSafe(crossinline f: () -> FxOf<Any?>): Fx<Any?> =
    try {
      f().fix()
    } catch (e: Throwable) {
      Fx.RaiseError(e.nonFatalOrThrow())
    }

  /**
   * Pops the next bind function from the stack, but filters out [FxFrame.ErrorHandler] references,
   * because we know they won't do anything since no error occurred — an optimization for skipping [handleErrorWith].
   */
  @PublishedApi
  internal fun popNextBind(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): ((Any?) -> Fx<Any?>)? =
    when {
      bFirst != null && bFirst !is FxFrame.Companion.ErrorHandler -> bFirst
      bRest != null -> {
        var cursor: ((Any?) -> Fx<Any?>)? = null
        while (cursor == null && bRest.isNotEmpty()) {
          val ref = bRest.pop()
          if (ref !is FxFrame.Companion.ErrorHandler) cursor = ref
        }
        cursor
      }
      else -> null
    }

  /** Specialisation of [FxFrame] to restore the old context regardless of success or failure. */
  //TODO write law in ConcurrentLaws to check if is cancelable after bracket.
  @PublishedApi
  internal class RestoreContext(
    val old: FxConnection,
    val restore: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection) : FxFrame<Any?, Fx<Any?>> {

    override fun invoke(a: Any?): Fx<Any?> = Fx.ConnectionSwitch(Fx.Pure(a, 0), { current ->
      restore(a, null, old, current)
    })

    override fun recover(e: Throwable): Fx<Any?> = Fx.ConnectionSwitch(Fx.RaiseError(e), { current ->
      restore(null, e, old, current)
    })
  }

  /**
   * An [AsyncBoundary] gets created only once, per [startCancelable] or [start] invocation.
   * The job of the [AsyncBoundary] is to provide a means of jumping in -and out of the run loop when awaiting an async result.
   *
   * To be able to do this it needs to have following capabilities:
   *   - It needs to save the state of the run loop and restore it when jumping back.
   *   State consist of the first [Fx.FlatMap.fb] [bFirst] and the following [Fx.FlatMap.fb] as an [Platform.ArrayStack] [bRest].
   *
   *   - It needs to act as a callback itself, so it can be called from outside.
   *   So it implements `(Either<Throwable, Any?>) -> Unit` which can model any callback.
   *
   *   - Bridge between `Kotlin`'s suspended world and the normal world.
   *   So we implement `Continuation<Any?>`, which has shape `(Result<Any?>) -> Unit` so we handle it like any other generic callback.
   *
   *   - Trampoline between consecutive async boundaries.
   *   It fills the stack by calling `loop` to jump back to the run loop and needs to be trampolined every [Platform.maxStackDepthSize] frames.
   *
   *   - Modify the running [CoroutineContext]
   *   This is necessary because we need to maintain the state within [kotlin.coroutines.coroutineContext].
   *
   * This instance is shared across a single [startCancelable] or [start] invocation to avoid an allocation / async boundary.
   *
   * The required capabilities are achieved by the help of 3 [Fx] cases, [Fx.Async], [Fx.ContinueOn], [Fx.Single].
   * They all have their respective [start] method.
   *
   * **IMPORTANT** this mechanism is essential to [Fx] and its [FxRunLoop] because this allows us to go from `suspend () -> A` to `A`.
   * Using that power we can write the `loop` in such a way that it is not suspended and as a result we have full control over the `Continuation`
   * This means it cannot sneak up on us and throw us out of the loop and thus adds support for pattern promoted by kotlinx. i.e.
   *
   * ```
   * Fx {
   *   suspendCoroutine<A> { cont ->
   *     cont.resumeWithException(RuntimeException("When I occur in a suspended runloop I exit/throw immediately"))
   *   }
   * }
   * ```
   */
  @PublishedApi
  internal class AsyncBoundary(connInit: FxConnection, val cb: (Either<Throwable, Any?>) -> Unit) : (Either<Throwable, Any?>) -> Unit, Continuation<Any?>, () -> Unit {

    //Instance state
    private var conn: FxConnection = connInit
    private var canCall = false
    private var contIndex: Int = 1
    private var jumpingContext = false

    //loop state
    private var bFirst: ((Any?) -> Fx<Any?>)? = null
    private var bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)? = null

    //async result
    private var result: Fx<Any?>? = null

    private inline val shouldTrampoline inline get() = contIndex == Platform.maxStackDepthSize

    fun contextSwitch(conn: FxConnection): Unit {
      this.conn = conn
    }

    fun start(fx: Fx.Async<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex++
      _context = ctx
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest

      // Run the users FFI function provided with the connection for cancellation support and [AsyncBoundary] as a generic callback.
      fx.proc(conn, this)
    }

    fun start(fx: Fx.Single<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
      _context = ctx

      //Run `suspend () -> A` with `AsyncBoundary` as `Continuation`
      fx.source.startCoroutine(this)
    }

    fun start(fx: Fx.ContinueOn<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex = 0
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
      result = fx.source as Fx<Any?>
      jumpingContext = true

      // Store the modified `CoroutineContext` and run it.
      // This will result in the correct state in a following suspend function. See `FxTest`.
      _context = ctx + fx.ctx
      suspendMapUnit.startCoroutine(this)
    }

    //NASTY TRICK!!!! Overwrite getter to var mutable backing field.
    // This allows us to reuse this instance across multiple context switches which allows us to stay more lightweight.
    private var _context: CoroutineContext = EmptyCoroutineContext
    override val context: CoroutineContext
      get() = _context

    override fun resumeWith(a: Result<Any?>): Unit {
      if (jumpingContext) { //If we're doing a context switch we already provided the result.
        jumpingContext = false
        if (result == null) throw Impossible
      } else {
        result = a.fold(
          onSuccess = { Fx.Pure(it, 0) },
          onFailure = { Fx.RaiseError(it) }
        )
      }

      if (shouldTrampoline) {
        contIndex = 1
        Platform.trampoline(this)
      } else invoke()
    }

    override operator fun invoke(either: Either<Throwable, Any?>): Unit {
      result = when (either) {
        is Either.Left -> Fx.RaiseError(either.a)
        is Either.Right -> Fx.Pure(either.b, 0)
      }

      if (shouldTrampoline) {
        contIndex = 1
        Platform.trampoline(this)
      } else invoke()
    }

    override fun invoke(): Unit {
      if (canCall) {
        canCall = false
        val bFirst = bFirst
        val bRest = bRest
        this.bFirst = null //We need to clear the state so GC can cleanup if it wants to.
        this.bRest = null

        loop(requireNotNull(result) { "Fx bug, please contact support! https://arrow-kt.io" }, conn, _context, cb, this, bFirst, bRest)
      }
    }
  }

}
