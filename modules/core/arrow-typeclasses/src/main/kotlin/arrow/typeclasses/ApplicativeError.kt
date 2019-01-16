package arrow.typeclasses

import arrow.Kind
import arrow.core.*

/**
 * ank_macro_hierarchy(arrow.typeclasses.ApplicativeError)
 */
interface ApplicativeError<F, E> : Applicative<F> {

  fun <A> raiseError(e: E): Kind<F, A>

  fun <A> Kind<F, A>.handleErrorWith(f: (E) -> Kind<F, A>): Kind<F, A>

  fun <A> E.raiseError(dummy: Unit = Unit): Kind<F, A> =
    raiseError(this)

  fun <A> OptionOf<A>.fromOption(f: () -> E): Kind<F, A> =
    fix().fold({ raiseError<A>(f()) }, { just(it) })

  fun <A, EE> Either<EE, A>.fromEither(f: (EE) -> E): Kind<F, A> =
    fix().fold({ raiseError<A>(f(it)) }, { just(it) })

  fun <A> TryOf<A>.fromTry(f: (Throwable) -> E): Kind<F, A> =
    fix().fold({ raiseError<A>(f(it)) }, { just(it) })

  fun <A> Kind<F, A>.handleError(f: (E) -> A): Kind<F, A> =
    handleErrorWith { just(f(it)) }

  fun <A> Kind<F, A>.attempt(): Kind<F, Either<E, A>> =
    map { Right(it) }.handleErrorWith {
      just(Left(it))
    }

  fun <A> catch(recover: (Throwable) -> E, f: () -> A): Kind<F, A> =
    try {
      just(f())
    } catch (t: Throwable) {
      raiseError<A>(recover(t))
    }

  fun <A> ApplicativeError<F, Throwable>.catch(f: () -> A): Kind<F, A> =
    catch(::identity, f)
}
