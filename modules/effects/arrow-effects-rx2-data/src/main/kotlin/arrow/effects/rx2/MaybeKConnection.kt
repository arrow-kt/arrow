package arrow.effects.rx2

import arrow.Kind
import arrow.core.Either
import arrow.effects.KindConnection
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer

typealias MaybeKConnection = KindConnection<ForMaybeK>
typealias MaybeKProc<A> = (MaybeKConnection, (Either<Throwable, A>) -> Unit) -> Unit
typealias MaybeKProcF<A> = (MaybeKConnection, (Either<Throwable, A>) -> Unit) -> Kind<ForMaybeK, Unit>

/**
 * Connection for [MaybeK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see MaybeK.async
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
fun MaybeKConnection(dummy: Unit = Unit): KindConnection<ForMaybeK> = KindConnection(object : MonadDefer<ForMaybeK> {
  override fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
    MaybeK.defer(fa)

  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)

  override fun <A, B> MaybeKOf<A>.flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> =
    MaybeK.tailRecM(a, f)

  override fun <A, B> MaybeKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>, use: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe({}, {}) }
