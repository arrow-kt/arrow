package arrow.effects

import arrow.core.Either
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.handleErrorWith as handleErrorW

fun IOConnection.toDisposable(): Disposable = { cancel().fix().unsafeRunSync() }
typealias IOConnection = KindConnection<ForIO>

fun IOConnection(dummy: Unit = Unit): IOConnection = KindConnection(MD)
internal val KindConnection.Companion.uncancelable: IOConnection get() = uncancelable(MD)

private object MD : MonadDefer<ForIO> {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    arrow.effects.IO.defer(fa)

  override fun <A> raiseError(e: Throwable): IO<A> =
    arrow.effects.IO.raiseError(e)

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    handleErrorW(f)

  override fun <A> just(a: A): IO<A> =
    arrow.effects.IO.just(a)

  override fun <A, B> IOOf<A>.flatMap(f: (A) -> IOOf<B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
    arrow.effects.IO.tailRecM(a, f)

  override fun <A, B> IOOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracketCase(release = { a, e -> release(a, e).fix() }, use = { use(it).fix() })
}
