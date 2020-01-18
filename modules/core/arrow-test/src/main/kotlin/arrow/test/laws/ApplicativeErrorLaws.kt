package arrow.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.fx.IO
import arrow.test.generators.GenK
import arrow.test.generators.applicativeError
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ApplicativeErrorLaws {

  fun <F, E> laws(
    AE: ApplicativeError<F, E>,
    GENK: GenK<F>,
    genE: Gen<E>,
    EQK: EqK<F>
  ): List<Law> {

    val genA = Gen.int()

    val EQ = EQK.liftEq(Int.eq())
    val EQ_EITHER = EQK.liftEq(Either.eq(Eq.any(), Int.eq()))

    return ApplicativeLaws.laws(AE, GENK, EQK) + listOf(
      Law("Applicative Error Laws: handle") { AE.applicativeErrorHandle(genA, genE, EQ) },
      Law("Applicative Error Laws: handle with for error") { AE.applicativeErrorHandleWith(genA, genE, EQ) },
      Law("Applicative Error Laws: handle with for success") { AE.applicativeErrorHandleWithPure(genA, genE, EQ) },
      Law("Applicative Error Laws: redeem is derived from map and handleError") { AE.redeemIsDerivedFromMapHandleError(genA, genE, EQ) },
      Law("Applicative Error Laws: attempt for error") { AE.applicativeErrorAttemptError(genE, EQ_EITHER) },
      Law("Applicative Error Laws: attempt for success") { AE.applicativeErrorAttemptSuccess(genA, EQ_EITHER) },
      Law("Applicative Error Laws: attempt lift from Either consistent with pure") { AE.applicativeErrorAttemptFromEitherConsistentWithPure(genA, genE, EQ_EITHER) }
    )
  }

  fun <F> laws(AE: ApplicativeError<F, Throwable>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {

    val GEN = Gen.int()
    val EQ = EQK.liftEq(Int.eq())

    return ApplicativeLaws.laws(AE, GENK, EQK) +
      laws(AE, GENK, Gen.throwable(), EQK) +
      listOf(
        Law("Applicative Error Laws: catch captures errors") { AE.applicativeErrorCatch(GEN, EQ) },
        Law("Applicative Error Laws: effectCatch captures errors") { AE.applicativeErrorEffectCatch(GEN, EQ) }
      )
  }

  fun <F, E, A> ApplicativeError<F, E>.applicativeErrorHandle(
    genA: Gen<A>,
    genE: Gen<E>,
    EQ: Eq<Kind<F, A>>
  ): Unit =
    forAll(Gen.functionAToB<E, A>(genA), genE) { f: (E) -> A, e: E ->
      raiseError<A>(e).handleError(f).equalUnderTheLaw(just(f(e)), EQ)
    }

  fun <F, E, A> ApplicativeError<F, E>.applicativeErrorHandleWith(genA: Gen<A>,
                                                                  genE: Gen<E>,
                                                                  EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.functionAToB<E, Kind<F, A>>(Gen.applicativeError(genA, genE, this)), genE) { f: (E) -> Kind<F, A>, e: E ->
      raiseError<A>(e).handleErrorWith(f).equalUnderTheLaw(f(e), EQ)
    }

  fun <F, E, A> ApplicativeError<F, E>.applicativeErrorHandleWithPure(genA: Gen<A>,
                                                                      genE: Gen<E>,
                                                                      EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.functionAToB<E, Kind<F, A>>(Gen.applicativeError(genA, genE, this)), genA) { f: (E) -> Kind<F, A>, a: A ->
      just(a).handleErrorWith(f).equalUnderTheLaw(just(a), EQ)
    }

  fun <F, E, A> ApplicativeError<F, E>.redeemIsDerivedFromMapHandleError(genA: Gen<A>,
                                                                         genE: Gen<E>,
                                                                         EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.applicativeError(genA, genE, this), Gen.functionAToB<E, A>(genA), Gen.functionAToB<A, A>(genA)) { fa, fe, fb ->
      fa.redeem(fe, fb).equalUnderTheLaw(fa.map(fb).handleError(fe), EQ)
    }

  fun <F, E, A> ApplicativeError<F, E>.applicativeErrorAttemptError(genE: Gen<E>,
                                                                    EQ: Eq<Kind<F, Either<E, A>>>): Unit =
    forAll(genE) { e: E ->
      raiseError<A>(e).attempt().equalUnderTheLaw(just(Left(e)), EQ)
    }

  fun <F, E, A> ApplicativeError<F, E>.applicativeErrorAttemptSuccess(genA: Gen<A>,
                                                                      EQ: Eq<Kind<F, Either<E, A>>>): Unit =
    forAll(genA) { a: A ->
      just(a).attempt().equalUnderTheLaw(just(Right(a)), EQ)
    }

  fun <F, E, A> ApplicativeError<F, E>.applicativeErrorAttemptFromEitherConsistentWithPure(genA: Gen<A>,
                                                                                           genE: Gen<E>,
                                                                                           EQ: Eq<Kind<F, Either<E, A>>>): Unit =
    forAll(Gen.either(genE, genA)) { either: Either<E, A> ->
      either.fromEither { it }.attempt().equalUnderTheLaw(just(either), EQ)
    }

  fun <F, A> ApplicativeError<F, Throwable>.applicativeErrorCatch(genA: Gen<A>,
                                                                  EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.either(Gen.throwable(), genA)) { either: Either<Throwable, A> ->
      catch { either.fold({ throw it }, ::identity) }.equalUnderTheLaw(either.fold({ raiseError<A>(it) }, { just(it) }), EQ)
    }

  fun <F, A> ApplicativeError<F, Throwable>.applicativeErrorEffectCatch(genA: Gen<A>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.either(Gen.throwable(), genA)) { either: Either<Throwable, A> ->
      IO.effect {
        effectCatch { either.fold({ throw it }, ::identity) }
      }.unsafeRunSync().equalUnderTheLaw(either.fold({ raiseError<A>(it) }, { just(it) }), EQ)
    }
}
