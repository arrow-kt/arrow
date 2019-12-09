@file:Suppress("UnusedImports")
package arrow.fx

import arrow.core.Either
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.handleErrorWith as handleErrorW

fun IOConnection.toDisposable(): Disposable = { cancel().fix().unsafeRunSync() }
typealias IOConnection = KindConnection<ForIO>

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun IOConnection(dummy: Unit = Unit): IOConnection = KindConnection(MD) { it.fix().unsafeRunAsync { } }

private val _uncancelable = KindConnection.uncancelable(MD)
internal inline val KindConnection.Companion.uncancelable: IOConnection
  inline get() = _uncancelable

private object MD : MonadDefer<ForIO> {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    arrow.fx.IO.defer(fa)

  override fun <A> raiseError(e: Throwable): IO<A> =
    arrow.fx.IO.raiseError(e)

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    handleErrorW(f)

  override fun <A> just(a: A): IO<A> =
    arrow.fx.IO.just(a)

  override fun <A, B> IOOf<A>.flatMap(f: (A) -> IOOf<B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
    arrow.fx.IO.tailRecM(a, f)

  override fun <A, B> IOOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracketCase(release = { a, e -> release(a, e).fix() }, use = { use(it).fix() })
}
