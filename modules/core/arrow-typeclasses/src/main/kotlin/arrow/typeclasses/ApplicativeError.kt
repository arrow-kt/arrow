package arrow.typeclasses

import arrow.*
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right

@typeclass
interface ApplicativeError<F, E> : Applicative<F>, TC {

    fun <A> raiseError(e: E): Kind<F, A>

    fun <A> handleErrorWith(fa: Kind<F, A>, f: (E) -> Kind<F, A>): Kind<F, A>

    fun <A> handleError(fa: Kind<F, A>, f: (E) -> A): Kind<F, A> = handleErrorWith(fa) { pure(f(it)) }

    fun <A> attempt(fa: Kind<F, A>): Kind<F, Either<E, A>> =
            handleErrorWith(map(fa) { Right(it) }) {
                pure(Left(it))
            }

    fun <A> fromEither(fab: Either<E, A>): Kind<F, A> = fab.fold({ raiseError<A>(it) }, { pure(it) })

    fun <A> catch(f: () -> A, recover: (Throwable) -> E): Kind<F, A> =
            try {
                pure(f())
            } catch (t: Throwable) {
                raiseError<A>(recover(t))
            }
}