package arrow.fx.rx2

import arrow.core.Either
import arrow.fx.KindConnection
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer

typealias SingleKProc<A> = ((Either<Throwable, A>) -> Unit) -> Unit
typealias SingleKProcF<A> = ((Either<Throwable, A>) -> Unit) -> SingleKOf<Unit>

/**
 * Connection for [SingleK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see SingleK.async
 */
@Suppress("UNUSED_PARAMETER", "FunctionName")
fun SingleKConnection(dummy: Unit = Unit): KindConnection<ForSingleK> = KindConnection(object : MonadDefer<ForSingleK> {
  override fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
    SingleK.defer(fa)

  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith(f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)

  override fun <A, B> SingleKOf<A>.flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> SingleKOf<Either<A, B>>): SingleK<B> =
    SingleK.tailRecM(a, f)

  override fun <A, B> SingleKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> SingleKOf<Unit>, use: (A) -> SingleKOf<B>): SingleK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.value().subscribe({}, {}) }
