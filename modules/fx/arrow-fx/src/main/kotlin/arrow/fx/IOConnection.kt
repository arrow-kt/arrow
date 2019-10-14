@file:Suppress("UnusedImports")

package arrow.fx

import arrow.core.Either
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.handleErrorWith as handleErrorW

typealias IOConnection<E> = KindConnection<IOPartialOf<E>>

fun <E> IOConnection<E>.toDisposable(): Disposable = { cancel().fix().unsafeRunSync() }

@Suppress("UNUSED_PARAMETER", "FunctionName")
fun <E> IOConnection(dummy: Unit = Unit): IOConnection<E> = KindConnection(MD()) { it.fix().unsafeRunAsync { } }

private val _uncancelable: IOConnection<Throwable> = KindConnection.uncancelable(MD())
internal inline val KindConnection.Companion.uncancelable: IOConnection<Throwable>
  inline get() = _uncancelable

private fun <E> MD() = object : MonadDefer<IOPartialOf<E>, E> {
  override fun <A> defer(fe: (Throwable) -> E, fa: () -> IOOf<E, A>): IO<E, A> =
    IO.defer(fe, fa)

  override fun <A> raiseError(e: E): IO<E, A> =
    IO.raiseError(e)

  override fun <A> IOOf<E, A>.handleErrorWith(f: (E) -> IOOf<E, A>): IO<E, A> =
    handleErrorW(f)

  override fun <A> just(a: A): IO<E, A> =
    IO.just(a)

  override fun <A, B> IOOf<E, A>.flatMap(f: (A) -> IOOf<E, B>): IO<E, B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<E, Either<A, B>>): IO<E, B> =
    IO.tailRecM(a, f)

  override fun <A, B> IOOf<E, A>.bracketCase(release: (A, ExitCase<E>) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<E, B> =
    fix().bracketCase(release = { a, e -> release(a, e).fix() }, use = { use(it).fix() })
}
