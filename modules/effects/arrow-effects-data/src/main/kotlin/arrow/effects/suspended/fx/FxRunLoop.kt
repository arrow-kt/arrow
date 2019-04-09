package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.NonFatal
import arrow.core.nonFatalOrThrow
import arrow.effects.*
import arrow.effects.IORunLoop.startCancelable
import arrow.effects.internal.Platform
import kotlin.coroutines.*

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal object FxRunLoop {

  suspend operator fun <A> invoke(source: FxOf<A>): A = suspendCoroutine { cont ->
    FxRunLoop.start(source) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  fun <A> start(source: FxOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    FxRunLoop.loop(source, NonCancelable, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  fun <A> startCancelable(fa: FxOf<A>,
                          token: CancelContext,
                          cb: (Either<Throwable, A>) -> Unit): Unit =
    FxRunLoop.loop(fa, token, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  @Suppress("CollapsibleIfStatements", "ReturnCount")
  fun loop(fa: FxOf<Any?>,
                    currToken: CancelContext,
                    cb: (Either<Throwable, Any?>) -> Unit,
                    rcbRef: RestartCallback?,
                    bFirstRef: ((Any?) -> Fx<Any?>)?,
                    bRestRef: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): Unit {

    val token: CancelContext = currToken
    var source: Fx<Any?>? = fa as Fx<Any?>
    var restartCallback: RestartCallback? = rcbRef
    var bFirst: ((Any?) -> Fx<Any?>)? = bFirstRef
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = bRestRef
    var hasResult = false
    var result: Any? = null

    while (true) {
      val isCancelled = token.connection.isCanceled()
      if (isCancelled) {
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
            } //An alternative to throwing would be nice..
            else -> {
              val error = (source as Fx.RaiseError<Any?>).error
              source = executeSafe { errorHandler.recover(error) }
              bFirst = null
            }
          }
        }
        PureTag -> {
          result = (source as Fx.Pure<Any?>).value
          hasResult = true
        }
        SingleTag -> {
          if (restartCallback == null) {
            restartCallback = RestartCallback(token.connection, cb)
          }
          restartCallback.prepare(bFirst, bRest)

          //Run the suspend function in the async boundary and return
          (source as Fx.Single<Any?>).source.startCoroutine(restartCallback)
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
        ConnectionSwitchTag -> {
          source as Fx.ConnectionSwitch<Any?>
          val next = source.source
          val modify = source.modify
          val restore = source.restore

          val old = token.connection
          token.connection = modify(old)
          source = next as? Fx<Any?>
          if (token.connection != old) {
            //We don't have this yet... RestartCallback is used to eliminate the need of Async boundary allocations.
            restartCallback?.contextSwitch(token.connection)
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
   * A `RestartCallback` gets created only once, per [startCancelable] (`unsafeRunAsync`) invocation, once an `Async`
   * state is hit, its job being to resume the loop after the boundary, but with the bind call-stack restored.
   */
  //TODO double check that `EmptyCoroutineContext + CancelContext(conn)` is actually what we want here.
  @PublishedApi
  internal class RestartCallback(connInit: FxConnection, val cb: (Either<Throwable, Any?>) -> Unit) : (Either<Throwable, Any?>) -> Unit, Continuation<Any?> {

    private var conn: FxConnection = connInit
    private var canCall = false
    private var bFirst: ((Any?) -> Fx<Any?>)? = null
    private var bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)? = null

    fun contextSwitch(conn: FxConnection): Unit {
      this.conn = conn
    }

    fun prepare(bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
    }

    override val context: CoroutineContext
      get() = EmptyCoroutineContext + CancelContext(conn)

    override fun resumeWith(result: Result<Any?>) {
      if (canCall) {
        canCall = false
        val source = result.fold(
          onSuccess = { Fx.Pure(it, 0) },
          onFailure = { Fx.RaiseError<Any?>(it) }
        )

        FxRunLoop.loop(source, CancelContext(conn), cb, this, bFirst, bRest)
      }
    }

    override operator fun invoke(either: Either<Throwable, Any?>): Unit {
      if (canCall) {
        canCall = false
        val source: Fx<Any?> = when (either) {
          is Either.Left -> Fx.RaiseError(either.a)
          is Either.Right -> Fx.Pure(either.b, 0)
        }

        FxRunLoop.loop(source, CancelContext(conn), cb, this, bFirst, bRest)
      }
    }
  }

}
