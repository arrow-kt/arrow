package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.OptionOf
import arrow.core.Right
import arrow.core.TryOf
import arrow.core.fix
import arrow.core.identity
import arrow.core.nonFatalOrThrow

/**
 * ank_macro_hierarchy(arrow.typeclasses.ApplicativeError)
 */
interface ApplicativeError<F, E> : Applicative<F> {

  fun <A> raiseError(e: E): Kind<F, A>

  fun <A> Kind<F, A>.handleErrorWith(f: (E) -> Kind<F, A>): Kind<F, A>

  fun <A> E.raiseError(dummy: Unit = Unit): Kind<F, A> =
    raiseError(this)

  fun <A> OptionOf<A>.fromOption(f: () -> E): Kind<F, A> =
    fix().fold({ raiseError(f()) }, { just(it) })

  fun <A, EE> Either<EE, A>.fromEither(f: (EE) -> E): Kind<F, A> =
    fix().fold({ raiseError(f(it)) }, { just(it) })

  @Deprecated(
    "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
    ReplaceWith("Either<EE, A>.fromEither(f)")
  )
  fun <A> TryOf<A>.fromTry(f: (Throwable) -> E): Kind<F, A> =
    fix().fold({ raiseError(f(it)) }, { just(it) })

  fun <A> Kind<F, A>.handleError(f: (E) -> A): Kind<F, A> =
    handleErrorWith { just(f(it)) }

  fun <A, B> Kind<F, A>.redeem(fe: (E) -> B, fb: (A) -> B): Kind<F, B> =
    map(fb).handleError(fe)

  fun <A> Kind<F, A>.attempt(): Kind<F, Either<E, A>> =
    map { Right(it) }.handleErrorWith {
      just(Left(it))
    }

  @Deprecated(
    "ApplicativeError#catch will be changed to a suspend fun in future versions",
    ReplaceWith("effectCatch(recover, f)")
  )
  fun <A> catch(recover: (Throwable) -> E, f: () -> A): Kind<F, A> =
    try {
      just(f())
    } catch (t: Throwable) {
      raiseError(recover(t.nonFatalOrThrow()))
    }

  @Deprecated(
    "ApplicativeError#catch will be changed to a suspend fun in future versions",
    ReplaceWith("effectCatch(f)")
  )
  fun <A> ApplicativeError<F, Throwable>.catch(f: () -> A): Kind<F, A> =
    catch(::identity, f)

  suspend fun <A> effectCatch(fe: (Throwable) -> E, f: suspend () -> A): Kind<F, A> =
    try { just(f()) } catch (t: Throwable) { raiseError(fe(t)) }

  suspend fun <F, A> ApplicativeError<F, Throwable>.effectCatch(f: suspend () -> A): Kind<F, A> =
    try { just(f()) } catch (t: Throwable) { raiseError(t) }
}
