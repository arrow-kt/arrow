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

  private fun <F> monadErrorLaws(M: MonadError<F, Throwable>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return listOf(
      Law("Monad Error Laws: left zero") { M.monadErrorLeftZero(EQ) },
      Law("Monad Error Laws: ensure consistency") { M.monadErrorEnsureConsistency(EQ) },
      Law("Monad Error Laws: NonFatal is caught") { M.monadErrorCatchesNonFatalThrowables(EQ) },
      Law("Monad Error Laws: Fatal errors are thrown") { M.monadErrorThrowsFatalThrowables(EQ) },
      Law("Monad Error Laws: redeemWith is derived from flatMap & HandleErrorWith") { M.monadErrorDerivesRedeemWith(EQ) },
      Law("Monad Error Laws: redeemWith pure is flatMap") { M.monadErrorRedeemWithPureIsFlatMap(EQ) }
    )
  }

  fun <F> laws(M: MonadError<F, Throwable>, GENK: GenK<F>, EQK: EqK<F>): List<Law> =
    MonadLaws.laws(M, GENK, EQK) +
      ApplicativeErrorLaws.laws(M, GENK, EQK) +
      monadErrorLaws(M, EQK)

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
      monadErrorLaws(M, EQK)

  fun <F> MonadError<F, Throwable>.monadErrorLeftZero(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicativeError(this)), Gen.throwable()) { f: (Int) -> Kind<F, Int>, e: Throwable ->
      raiseError<Int>(e).flatMap(f).equalUnderTheLaw(raiseError(e), EQ)
    }

  fun <F> MonadError<F, Throwable>.monadErrorEnsureConsistency(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().applicativeError(this), Gen.throwable(), Gen.functionAToB<Int, Boolean>(Gen.bool())) { fa: Kind<F, Int>, e: Throwable, p: (Int) -> Boolean ->
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

  fun <F> MonadError<F, Throwable>.monadErrorDerivesRedeemWith(EQ: Eq<Kind<F, Int>>) =
    forAll(Gen.int().applicativeError(this),
      Gen.functionAToB<Throwable, Kind<F, Int>>(Gen.int().applicativeError(this)),
      Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicative(this))) { fa, fe, fb ->
      fa.redeemWith(fe, fb).equalUnderTheLaw(fa.flatMap(fb).handleErrorWith(fe), EQ)
    }

  fun <F> MonadError<F, Throwable>.monadErrorRedeemWithPureIsFlatMap(EQ: Eq<Kind<F, Int>>) =
    forAll(Gen.int().applicative(this),
      Gen.functionAToB<Throwable, Kind<F, Int>>(Gen.int().applicativeError(this)),
      Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicative(this))) { fa, fe, fb ->
      fa.redeemWith(fe, fb).equalUnderTheLaw(fa.flatMap(fb), EQ)
    }
}
