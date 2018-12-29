package arrow.effects

import arrow.core.Either
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.handleErrorWith as handleErrorW

typealias DeferredKConnection = KindConnection<ForDeferredK>
typealias DeferredKProc<A> = (DeferredKConnection, (Either<Throwable, A>) -> Unit) -> Unit
typealias DeferredKProcF<A> = (DeferredKConnection, (Either<Throwable, A>) -> Unit) -> DeferredKOf<Unit>

/**
 * Connection for [DeferredK].
 *
 * A connection is represented by a composite of `cancel` functions,
 * [KindConnection.cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 *
 * @see DeferredK.async
 */
@Suppress("FunctionName")
fun DeferredKConnection(dummy: Unit = Unit): KindConnection<ForDeferredK> = KindConnection(object : MonadDefer<ForDeferredK> {
  override fun <A> defer(fa: () -> DeferredKOf<A>): DeferredK<A> =
    DeferredK.defer(fa = fa)

  override fun <A> raiseError(e: Throwable): DeferredK<A> =
    DeferredK.raiseError(e)

  override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
    handleErrorW(f)

  override fun <A> just(a: A): DeferredK<A> =
    DeferredK.just(a)

  override fun <A, B> DeferredKOf<A>.flatMap(f: (A) -> DeferredKOf<B>): DeferredK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> DeferredKOf<Either<A, B>>): DeferredK<B> =
    DeferredK.tailRecM(a, f)

  override fun <A, B> DeferredKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> DeferredKOf<Unit>, use: (A) -> DeferredKOf<B>): DeferredK<B> =
    fix().bracketCase(release = release, use = use)
}) { it.unsafeRunAsync { } }
