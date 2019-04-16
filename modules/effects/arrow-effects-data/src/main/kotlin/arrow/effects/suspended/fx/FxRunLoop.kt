package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.core.*
import arrow.effects.*
import arrow.effects.IORunLoop.startCancelable
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx.FxRunLoop.startCancelable
import kotlin.coroutines.*
import kotlin.coroutines.Continuation

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal object FxRunLoop {

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

  @Suppress("CollapsibleIfStatements", "ReturnCount")
  fun loop(fa: FxOf<Any?>,
           currToken: KindConnection<ForFx>,
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
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }

          asyncBoundary.continueOn(source as Fx.ContinueOn<Any?>, ctx, bFirst, bRest)
          return
        }
        PureTag -> {
          result = (source as Fx.Pure<Any?>).value
          hasResult = true
        }
        SingleTag -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }
          asyncBoundary.prepare(ctx, bFirst, bRest)

          //Run the suspend function in the async boundary and return
          (source as Fx.Single<Any?>).source.startCoroutine(asyncBoundary)
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

          asyncBoundary.prepare(ctx, bFirst, bRest)
          // Return case for Async operations
          source as Fx.Async<Any?>
          source.proc(conn, asyncBoundary)
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
   * Pops the next bind function from the stack,
   * but filters out `IOFrame.ErrorHandler` references, because we know they won't do anything — an optimization for [handleErrorWith].
   */
  @PublishedApi
  internal fun popNextBind(
    bFirst: ((Any?) -> Fx<Any?>)?,
    bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?
  ): ((Any?) -> Fx<Any?>)? =
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

  //TODO write law in ConcurrentLaws to check if is cancelable after bracket.
  @PublishedApi
  internal class RestoreContext(
    val old: FxConnection,
    val restore: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection) : FxFrame<Any?, Fx<Any?>> {

    override fun invoke(a: Any?): Fx<Any?> = Fx.ConnectionSwitch(Fx.Pure(a, 0), { current ->
      restore(a, null, old, current)
    }, null)

    override fun recover(e: Throwable): Fx<Any?> = Fx.ConnectionSwitch(Fx.RaiseError(e), { current ->
      restore(null, e, old, current)
    }, null)
  }

  /**
   * An [AsyncBoundary] gets created only once, per [startCancelable] or [start] invocation.
   * The job of the [AsyncBoundary] is to provide a means of jumping in -and out of the run loop when awaiting an async result.
   *
   * To be able to do this it needs to have following capabilities:
   *   - It needs to save the state of the run loop and restore it when jumping back. See [bFirst], [bRest].
   *
   *   - It needs to act as a callback itself, so it can be called from outside.
   *     So it implements `(Either<Throwable, Any?>) -> Unit` which can model any callback.
   *
   *   - Bridge between `Kotlin`'s suspended world and the normal world.
   *     So we implement `Continuation<Any?>`, which has shape `(Result<Any?>) -> Unit` so we handle it like any other generic callback.
   *
   *   - Trampoline between consecutive async boundaries.
   *     It fills the stack by calling `loop` to jump back to the run loop and needs to be trampolined every [Platform.maxStackDepthSize] frames.
   *
   * This instance is shared across a single  [startCancelable] or [start] invocation to avoid an allocation / async boundary.
   *
   * **IMPORTANT** this mechanism is essential to [Fx] and its [FxRunLoop] because this allows us to go from `suspend () -> A` to `A`.
   * Using that power we can write the `loop` in such a way that it is not suspended and as a result we have full control over the `Continuation`
   * This means it cannot sneak up on us and throw us out of the loop and thus adds support for pattern promoted by kotlinx. i.e.
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

    fun prepare(ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex++
      _context = ctx
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
    }

    fun continueOn(fx: Fx.ContinueOn<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex = 0
      _context = ctx + fx.ctx
      canCall = true
      jumpingContext = true
      this.bFirst = bFirst
      this.bRest = bRest
      result = fx.source as Fx<Any?>
      //We have to trigger `resumeWith` on a certain context to make the switch
      //Similarly on how you can switch context by intercepting a continuation and calling `resume` on a certain thread to trampoline.
      suspend { Unit }.startCoroutine(this)
    }

    private var _context: CoroutineContext = EmptyCoroutineContext
    override val context: CoroutineContext
      get() = _context

    override fun resumeWith(a: Result<Any?>): Unit {
      if (jumpingContext) {
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
