package arrow.effects.rx2

import arrow.core.Either
import arrow.effects.KindConnection
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer

typealias FlowableKConnection = KindConnection<ForFlowableK>
typealias FlowableKProc<A> = (FlowableKConnection, (Either<Throwable, A>) -> Unit) -> Unit
typealias FlowableKProcF<A> = (FlowableKConnection, (Either<Throwable, A>) -> Unit) -> FlowableKOf<Unit>

/**
 * Connection for [FlowableK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see FlowableK.async
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
fun FlowableKConnection(dummy: Unit = Unit): KindConnection<ForFlowableK> = KindConnection(object : MonadDefer<ForFlowableK> {
  override fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
    FlowableK.defer(fa)

  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): FlowableK<A> =
    FlowableK.just(a)

  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKOf<Either<A, B>>): FlowableK<B> =
    FlowableK.tailRecM(a, f)

  override fun <A, B> FlowableKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FlowableKOf<Unit>, use: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe({}, {}) }
