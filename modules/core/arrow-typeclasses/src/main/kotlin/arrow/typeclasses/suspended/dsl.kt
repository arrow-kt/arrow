package arrow.typeclasses.suspended

import arrow.Kind
import arrow.core.*
import arrow.typeclasses.*

interface BindSyntax<F> {
  suspend fun <A> Kind<F, A>.bind(): A
  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()
}

interface FunctorSyntax<F> : Functor<F>, BindSyntax<F> {
  suspend fun <A, B> Kind<F, A>.map(unit: Unit = Unit, f: (A) -> B): B =
    f(bind())
}

interface ApplicativeSyntax<F> : FunctorSyntax<F>, Applicative<F> {

  private suspend fun <A> applicative(fb: Applicative<F>.() -> Kind<F, A>): A =
    run<Applicative<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A, B, Z> Kind<F, A>.map(
    fb: Kind<F, B>,
    f: (Tuple2<A, B>) -> Z
  ): Z =
    applicative { map(this@map, fb, f) }

  suspend fun <A, B, C, Z> Kind<F, A>.map(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    f: (Tuple3<A, B, C>) -> Z
  ): Z =
    applicative { map(this@map, fb, fc, f) }

  suspend fun <A, B, C, D, Z> Kind<F, A>.map(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (Tuple4<A, B, C, D>) -> Z
  ): Z =
    applicative { map(this@map, fb, fc, fd, f) }

  suspend fun <A, B> Kind<F, A>.tupled(fb: Kind<F, B>): Tuple2<A, B> =
    super.tupled(this, fb).bind()

  suspend fun <A, B, C> Kind<F, A>.tupled(
    fb: Kind<F, B>,
    fc: Kind<F, C>
  ): Tuple3<A, B, C> =
    super.tupled(this, fb, fc).bind()

  suspend fun <A, B, C, D> Kind<F, A>.tupled(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>
  ): Tuple4<A, B, C, D> =
    super.tupled(this, fb, fc, fd).bind()

  suspend fun <A, B, C, D, E> Kind<F, A>.tupled(
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>
  ): Tuple5<A, B, C, D, E> =
    super.tupled(this, fb, fc, fd, fe).bind()

}

interface ApplicativeErrorSyntax<F, E> : ApplicativeError<F, E>, ApplicativeSyntax<F> {
  suspend fun <A> E.raiseError(): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { raiseError(this@raiseError) }.bind()

  suspend fun <A> Kind<F, A>.handleErrorWith(unit: Unit = Unit, f: (E) -> Kind<F, A>): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { handleErrorWith(f) }.bind()

  suspend fun <A> OptionOf<A>.fromOption(unit: Unit = Unit, f: () -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@fromOption.fromOption(f) }.bind()

  suspend fun <A, B> Either<B, A>.fromEither(unit: Unit = Unit, f: (B) -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@fromEither.fromEither(f) }.bind()

  suspend fun <A> TryOf<A>.fromTry(unit: Unit = Unit, f: (Throwable) -> E): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@fromTry.fromTry(f) }.bind()

  suspend fun <A> Kind<F, A>.handleError(unit: Unit = Unit, f: (E) -> A): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { this@handleError.handleError(f) }.bind()

  suspend fun <A> Kind<F, A>.toEither(unit: Unit = Unit): Either<E, A> =
    run<ApplicativeError<F, E>, Kind<F, Either<E, A>>> { this@toEither.attempt() }.bind()

  suspend fun <A> catch(unit: Unit = Unit, recover: (Throwable) -> E, f: () -> A): A =
    run<ApplicativeError<F, E>, Kind<F, A>> { catch(recover, f) }.bind()

  suspend fun <A> ApplicativeError<F, Throwable>.catch(unit: Unit = Unit, f: () -> A): A =
    catch(::identity, f).bind()

}

interface MonadSyntax<F> : ApplicativeSyntax<F>, Monad<F> {
  suspend fun <A> Kind<F, A>.followedBy(fb: Kind<F, A>): A =
    run<Monad<F>, Kind<F, A>> { followedBy(fb) }.bind()

  suspend fun <A> Kind<F, A>.forEffect(fb: Kind<F, A>): A =
    run<Monad<F>, Kind<F, A>> { forEffect(fb) }.bind()

  suspend fun <A> Kind<F, A>.effectM(f: (A) -> Kind<F, A>): A =
    run<Monad<F>, Kind<F, A>> { effectM(f) }.bind()

  suspend fun <A> Kind<F, A>.mproduct(f: (A) -> Kind<F, A>): Tuple2<A, A> =
    run<Monad<F>, Kind<F, Tuple2<A, A>>> { mproduct(f) }.bind()

  suspend fun <A> Kind<F, Boolean>.ifM(ifTrue: () -> Kind<F, A>, ifFalse: () -> Kind<F, A>, unit: Unit = Unit): A =
    run<Monad<F>, Kind<F, A>> { ifM(ifTrue, ifFalse) }.bind()

  suspend fun <A> Kind<F, Kind<F, A>>.flatten(unit: Unit = Unit): A =
    bind().bind()

  suspend fun <A, B> Kind<F, A>.flatMap(unit: Unit = Unit, f: (A) -> Kind<F, B>): B =
    f(bind()).bind()
}

interface MonadErrorSyntax<F, E> : MonadSyntax<F>, MonadError<F, E> {
  suspend fun <A> Kind<F, A>.ensure(unit: Unit = Unit, error: () -> E, predicate: (A) -> Boolean): A =
    run<Monad<F>, Kind<F, A>> { ensure(error, predicate) }.bind()
}
