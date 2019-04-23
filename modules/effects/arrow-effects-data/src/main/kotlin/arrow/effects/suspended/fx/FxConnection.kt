package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.effects.*
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.mapUnit

fun FxConnection.toDisposable(): Disposable = { FxRunLoop.start(cancel(), cb = mapUnit) }
typealias FxConnection = KindConnection<ForFx>

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun FxConnection(dummy: Unit = Unit): FxConnection = KindConnection(MD) { FxRunLoop.start(it, cb = mapUnit) }
val FxNonCancelable = KindConnection.uncancelable(MD)

private object MD : MonadDefer<ForFx> {
  override fun <A> defer(fa: () -> FxOf<A>): Fx<A> = Fx.defer { fa() }
  override fun <A> raiseError(e: Throwable): Fx<A> = Fx.raiseError(e)
  override fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> = fix().handleErrorWith(f)
  override fun <A> just(a: A): Fx<A> = Fx.just(a)
  override fun <A, B> FxOf<A>.flatMap(f: (A) -> FxOf<B>): Fx<B> = fix().flatMap(f)
  override fun <A, B> tailRecM(a: A, f: (A) -> FxOf<Either<A, B>>): Fx<B> = Fx.tailRecM(a, f)
  override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> = fix().bracketCase(release, use)
}