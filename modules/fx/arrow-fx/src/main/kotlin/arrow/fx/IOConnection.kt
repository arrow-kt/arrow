@file:Suppress("UnusedImports")
package arrow.fx

import arrow.core.Either
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.handleErrorWith as handleErrorW

fun IOConnection.toDisposable(): Disposable = { cancel().fix().unsafeRunSync() }
typealias IOConnection = KindConnection<IOPartialOf<Throwable>>

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun IOConnection(dummy: Unit = Unit): IOConnection = KindConnection(MD) { it.fix().unsafeRunAsync { } }

private val _uncancelable = KindConnection.uncancelable(MD)
internal inline val KindConnection.Companion.uncancelable: IOConnection
  inline get() = _uncancelable

private object MD : MonadDefer<IOPartialOf<Throwable>> {
  override fun <A> defer(fa: () -> IOOf<Throwable, A>): IO<Throwable, A> =
    IO.defer(fa)

  override fun <A> raiseError(e: Throwable): IO<Throwable, A> =
    IO.raiseError(e)

  override fun <A> IOOf<Throwable, A>.handleErrorWith(f: (Throwable) -> IOOf<Throwable, A>): IO<Throwable, A> =
    handleErrorW(f)

  override fun <A> just(a: A): IO<Throwable, A> =
    IO.just(a)

  override fun <A, B> IOOf<Throwable, A>.flatMap(f: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Throwable, Either<A, B>>): IO<Throwable, B> =
    IO.tailRecM(a, f)

  override fun <A, B> IOOf<Throwable, A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().bracketCase(release = { a, e -> release(a, e).fix() }, use = { use(it).fix() })
}
