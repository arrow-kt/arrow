package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.test.generators.applicative
import arrow.test.generators.applicativeError
import arrow.test.generators.fatalThrowable
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadError
import arrow.typeclasses.Selective
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldThrowAny

object MonadErrorLaws {

  private fun <F, E> monadErrorLaws(M: MonadError<F, E>, genE: Gen<E>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val genA = Gen.int()

    return listOf(
      Law("Monad Error Laws: left zero") { M.monadErrorLeftZero(genA, genE, EQ) },
      Law("Monad Error Laws: ensure consistency") { M.monadErrorEnsureConsistency(genA, genE, EQ) },
      Law("Monad Error Laws: redeemWith is derived from flatMap & HandleErrorWith") { M.monadErrorDerivesRedeemWith(genA, genE, EQ) },
      Law("Monad Error Laws: redeemWith pure is flatMap") { M.monadErrorRedeemWithPureIsFlatMap(genA, genE, EQ) }
    )
  }

  private fun <F> monadErrorLaws(M: MonadError<F, Throwable>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return listOf(
      Law("Monad Error Laws: NonFatal is caught") { M.monadErrorCatchesNonFatalThrowables(EQ) },
      Law("Monad Error Laws: Fatal errors are thrown") { M.monadErrorThrowsFatalThrowables(EQ) }
    )
  }

  fun <F, E> laws(M: MonadError<F, E>, GENK: GenK<F>, genE: Gen<E>, EQK: EqK<F>): List<Law> =
    MonadLaws.laws(M, GENK, EQK) +
      ApplicativeErrorLaws.laws(M, GENK, genE, EQK) +
      monadErrorLaws(M, genE, EQK)

  fun <F> laws(M: MonadError<F, Throwable>, GENK: GenK<F>, EQK: EqK<F>): List<Law> =
    MonadLaws.laws(M, GENK, EQK) +
      ApplicativeErrorLaws.laws(M, GENK, EQK) +
      monadErrorLaws(M, EQK) +
      monadErrorLaws(M, Gen.throwable(), EQK)

  fun <F> laws(
    M: MonadError<F, Throwable>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(M, FF, AP, SL, GENK, EQK) +
      ApplicativeErrorLaws.laws(M, GENK, EQK) +
      monadErrorLaws(M, EQK) +
      monadErrorLaws(M, Gen.throwable(), EQK)

  fun <F, E, A> MonadError<F, E>.monadErrorLeftZero(genA: Gen<A>, genE: Gen<E>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.functionAToB<A, Kind<F, A>>(Gen.applicativeError(genA, genE, this)), genE) { f: (A) -> Kind<F, A>, e: E ->
      raiseError<A>(e).flatMap(f).equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F, E, A> MonadError<F, E>.monadErrorEnsureConsistency(genA: Gen<A>, genE: Gen<E>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.applicativeError(genA, genE, this), genE, Gen.functionAToB<A, Boolean>(Gen.bool())) { fa: Kind<F, A>, e: E, p: (A) -> Boolean ->
      fa.ensure({ e }, p).equalUnderTheLaw(fa.flatMap { a -> if (p(a)) just(a) else raiseError(e) }, EQ)
    }

  fun <F> MonadError<F, Throwable>.monadErrorCatchesNonFatalThrowables(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.throwable()) { nonFatal: Throwable ->
      catch { throw nonFatal }.equalUnderTheLaw(raiseError(nonFatal), EQ)
    }
  }

  fun <F> MonadError<F, Throwable>.monadErrorThrowsFatalThrowables(EQ: Eq<Kind<F, Int>>) {
    forAll(Gen.fatalThrowable()) { fatal: Throwable ->
      shouldThrowAny {
        fun <A> itShouldNotComeThisFar(): Kind<F, A> {
          fail("MonadError should rethrow the fatal Throwable: '$fatal'.")
        }

        catch { throw fatal }.equalUnderTheLaw(itShouldNotComeThisFar(), EQ)
      } == fatal
    }
  }

  fun <F, E, A> MonadError<F, E>.monadErrorDerivesRedeemWith(genA: Gen<A>, genE: Gen<E>, EQ: Eq<Kind<F, A>>) =
    forAll(genA.applicative(this),
      Gen.functionAToB<E, Kind<F, A>>(Gen.applicativeError(genA, genE, this)),
      Gen.functionAToB<A, Kind<F, A>>(genA.applicative(this))) { fa, fe, fb ->
      fa.redeemWith(fe, fb).equalUnderTheLaw(fa.flatMap(fb).handleErrorWith(fe), EQ)
    }

  fun <F, E, A> MonadError<F, E>.monadErrorRedeemWithPureIsFlatMap(genA: Gen<A>, genE: Gen<E>, EQ: Eq<Kind<F, A>>) =
    forAll(genA.applicative(this),
      Gen.functionAToB<E, Kind<F, A>>(Gen.applicativeError(genA, genE, this)),
      Gen.functionAToB<A, Kind<F, A>>(genA.applicative(this))) { fa, fe, fb ->
      fa.redeemWith(fe, fb).equalUnderTheLaw(fa.flatMap(fb), EQ)
    }
}
