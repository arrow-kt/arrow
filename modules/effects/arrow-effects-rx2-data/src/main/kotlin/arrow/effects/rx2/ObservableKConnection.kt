package arrow.effects.rx2

import arrow.core.Either
import arrow.effects.KindConnection
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer

typealias ObservableKConnection = KindConnection<ForObservableK>
typealias ObservableKProc<A> = (ObservableKConnection, (Either<Throwable, A>) -> Unit) -> Unit
typealias ObservableKProcF<A> = (ObservableKConnection, (Either<Throwable, A>) -> Unit) -> ObservableKOf<Unit>

/**
 * Connection for [ObservableK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see ObservableK.async
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
fun ObservableKConnection(dummy: Unit = Unit): KindConnection<ForObservableK> = KindConnection(object : MonadDefer<ForObservableK> {
  override fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
    ObservableK.defer(fa)

  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)

  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKOf<Either<A, B>>): ObservableK<B> =
    ObservableK.tailRecM(a, f)

  override fun <A, B> ObservableKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> ObservableKOf<Unit>, use: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe({}, {}) }
