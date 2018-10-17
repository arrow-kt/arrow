package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity

interface ApplicativeError<F, E> : Applicative<F> {

  fun <A> raiseError(e: E): Kind<F, A>

  fun <A> Kind<F, A>.handleErrorWith(f: (E) -> Kind<F, A>): Kind<F, A>

  fun <A> E.raiseError(dummy: Unit = Unit): Kind<F, A> =
    raiseError(this)

  fun <A> Kind<F, A>.handleError(f: (E) -> A): Kind<F, A> =
    handleErrorWith { just(f(it)) }

  fun <A> Kind<F, A>.attempt(): Kind<F, Either<E, A>> =
    map { Right(it) }.handleErrorWith {
      just(Left(it))
    }

  fun <A> fromEither(fab: Either<E, A>): Kind<F, A> =
    fab.fold({ raiseError<A>(it) }, { just(it) })

  fun <A> catch(f: () -> A, recover: (Throwable) -> E): Kind<F, A> =
    try {
      just(f())
    } catch (t: Throwable) {
      raiseError<A>(recover(t))
    }

  fun <A> ApplicativeError<F, Throwable>.catch(f: () -> A): Kind<F, A> =
    catch(f, ::identity)
}
