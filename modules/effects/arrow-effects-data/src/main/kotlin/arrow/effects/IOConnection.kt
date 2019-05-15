package arrow.effects

import arrow.core.Either
import arrow.effects.internal.IORunLoop
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.mapUnit
import arrow.effects.handleErrorWith as handleErrorW

fun IOConnection.toDisposable(): Disposable = { IORunLoop.start(cancel(), cb = mapUnit) }
typealias IOConnection = KindConnection<ForIO>

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun IOConnection(dummy: Unit = Unit): IOConnection = KindConnection(MD) { IORunLoop.start(it, cb = mapUnit) }
val IONonCancelable = KindConnection.uncancelable(MD)

private object MD : MonadDefer<ForIO> {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> = IO.Defer(fa)
  override fun <A> raiseError(e: Throwable): IO<A> = IO.RaiseError(e)
  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> = handleErrorW(f)
  override fun <A> just(a: A): IO<A> = IO.Pure(a)
  override fun <A, B> IOOf<A>.flatMap(f: (A) -> IOOf<B>): IO<B> = fix().flatMap(f)
  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> = IO.tailRecM(a, f)
  override fun <A, B> IOOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> = fix().bracketCase(release, use)
}
