package arrow.fx.reactor

import arrow.core.Either
import arrow.fx.KindConnection
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer

@Deprecated("Cancellation should be done with the cancelable combinator")
typealias FluxKProc<A> = (KindConnection<ForFluxK>, (Either<Throwable, A>) -> Unit) -> Unit
@Deprecated("Cancellation should be done with the cancelable combinator")
typealias FluxKProcF<A> = (KindConnection<ForFluxK>, (Either<Throwable, A>) -> Unit) -> FluxKOf<Unit>

/**
 * Connection for [FluxK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see FluxK.async
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
@Deprecated(message = "Cancelling operations through FluxKConnection will not be supported anymore." +
  "In case you need to cancel multiple processes can do so by using cancelable and composing cancel operations using zipWith or other parallel operators")
fun FluxKConnection(dummy: Unit = Unit): KindConnection<ForFluxK> = KindConnection(object : MonadDefer<ForFluxK> {
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
    FluxK.defer(fa)

  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)

  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> FluxKOf<Either<A, B>>): FluxK<B> =
    FluxK.tailRecM(a, f)

  override fun <A, B> FluxKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FluxKOf<Unit>, use: (A) -> FluxKOf<B>): FluxK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe({}, {}) }
