package arrow.fx.coroutines

import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

suspend fun <A> cancellable(cb: ((Result<A>) -> Unit) -> CancelToken): A =
  suspendCoroutine { cont ->
    val conn = cont.context.connection()
    val cbb2 = Platform.onceOnly(conn, cont::resumeWith)

    val cancellable = ForwardCancellable()
    conn.push(cancellable.cancel())

    if (conn.isNotCancelled()) {
      cancellable.complete(
        try {
          cb(cbb2)
        } catch (throwable: Throwable) {
          cbb2(Result.failure(throwable.nonFatalOrThrow()))
          CancelToken.unit
        }
      )
    } else cancellable.complete(CancelToken.unit)
  }

suspend fun <A> cancellableF(k: suspend ((Result<A>) -> Unit) -> CancelToken): A =
  suspendCoroutine { cont ->
    val conn = cont.context.connection()

    val state = AtomicRefW<((Result<Unit>) -> Unit)?>(null)
    val cb1 = { a: Result<A> ->
      try {
        cont.resumeWith(a)
      } finally {
        // compareAndSet can only succeed in case the operation is already finished
        // and no cancellation token was installed yet
        if (!state.compareAndSet(null, { Unit })) {
          val cb2 = state.value
          state.lazySet(null)
          cb2?.invoke(Result.success(Unit))
        }
      }
    }

    val conn2 = SuspendConnection()
    conn.push(conn2.cancelToken())

    suspend {
      // Until we've got a cancellation token, the task needs to be evaluated
      // uninterruptedly, otherwise risking a leak, hence the bracket
      // TODO CREATE KotlinTracker issue using CancelToken here breaks something in compilation
      bracketCase<suspend () -> Unit, Unit>(
        acquire = { k(cb1).cancel },
        use = { waitUntilCallbackInvoked(state) },
        release = { token, ex ->
          when (ex) {
            ExitCase.Cancelled -> token.invoke()
            else -> Unit
          }
        }
      )
    }.startCoroutineCancellable(CancellableContinuation(cont.context, conn2) {
      // TODO send CancelToken exception to Enviroment
      it.fold(::identity, Throwable::printStackTrace)
    })
  }

private suspend fun waitUntilCallbackInvoked(state: AtomicRefW<((Result<Unit>) -> Unit)?>) =
  suspendCoroutineUninterceptedOrReturn<Unit> { cont ->
    // compareAndSet succeeds if CancelToken returned & callback not invoked, suspend
    if (state.compareAndSet(null, cont::resumeWith)) COROUTINE_SUSPENDED
    else Unit // k invoked before token returned, finish immediately
  }

suspend fun <A> never(): A =
  suspendCoroutine<Nothing> {}

suspend fun unit(): Unit = Unit
