package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity

inline operator fun <F, A, E> ApplicativeError<F, E>.invoke(ff: ApplicativeError<F, E>.() -> A) =
        run(ff)

interface ApplicativeError<F, E> : Applicative<F> {

    fun <A> raiseError(e: E): Kind<F, A>

    fun <A> Kind<F, A>.handleErrorWith(f: (E) -> Kind<F, A>): Kind<F, A>

    fun <A> Kind<F, A>.handleError(f: (E) -> A): Kind<F, A> = handleErrorWith { pure(f(it)) }

    fun <A> Kind<F, A>.attempt(): Kind<F, Either<E, A>> =
            this.map { Right(it) }.handleErrorWith {
                pure(Left(it))
            }

    fun <A> fromEither(fab: Either<E, A>): Kind<F, A> = fab.fold({ raiseError<A>(it) }, { pure(it) })

    fun <A> catch(f: () -> A, recover: (Throwable) -> E): Kind<F, A> =
            try {
                pure(f())
            } catch (t: Throwable) {
                raiseError<A>(recover(t))
            }

    fun <A> ApplicativeError<F, Throwable>.catch(f: () -> A): Kind<F, A> =
            catch(f, ::identity)
}
