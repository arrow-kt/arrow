package arrow.streams.internal

import arrow.Kind
import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genThrowable
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FreeCTest : UnitSpec() {

  init {

    testLaws(FunctorLaws.laws(
      FF = FreeC.functor<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      f = { FreeC.pure(it) }
    ))

    testLaws(ApplicativeLaws.laws(
      A = FreeC.applicative<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
    ))

    testLaws(MonadLaws.laws(
      M = FreeC.monad<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
    ))

    testLaws(MonadErrorLaws.laws(
      M = FreeC.monadError<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      EQERR = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      EQ_EITHER = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
    ))

    testLaws(ApplicativeErrorLaws.laws(
      AE = FreeC.applicativeError<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      EQERR = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      EQ_EITHER = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
    ))

    testLaws(MonadDeferLaws.laws(
      SC = FreeC.monadDefer<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      EQERR = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      EQ_EITHER = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
    ))

    "Running a pure value" {
      forAll(Gen.string()) { s ->
        FreeC.pure<EitherPartialOf<Throwable>, String>(s)
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "Running an error value" {
      forAll(genThrowable()) { t ->
        FreeC.raiseError<EitherPartialOf<Throwable>, Int>(t)
          .run(Either.monadError()) == Left(t)
      }
    }

    "Running a interrupted value without errors" {
      FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), None)
        .run(Either.monadError()) == Right(None)
    }

    "Running a interrupted value with errors" {
      forAll(genThrowable()) { t ->
        FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), t.some())
          .run(Either.monadError()) == Left(t)
      }
    }

    "Running an Eval value" {
      forAll(Gen.string()) { s ->
        FreeC.liftF(s.right())
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "Running a Bind value" {
      forAll(Gen.string()) { s ->
        FreeC.Bind(FreeC.pure("")) { _ -> FreeC.pure<EitherPartialOf<Throwable>, String>(s) }
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "Running an suspended value"{
      forAll(Gen.string()) { s ->
        FreeC.defer() { FreeC.pure<EitherPartialOf<Throwable>, String>(s) }
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "map" {
      forAll(Gen.string(), genFunctionAToB<String, String>(Gen.string())) { s, f ->
        FreeC.pure<EitherPartialOf<Throwable>, String>(s)
          .map(f)
          .run(Either.monadError()) == Right(Some(f(s)))
      }
    }

    "flatMap"{
      forAll(Gen.string(), genFunctionAToB<String, String>(Gen.string())) { s, f ->
        FreeC.pure<EitherPartialOf<Throwable>, String>(s)
          .flatMap { FreeC.pure<EitherPartialOf<Throwable>, String>(f(it)) }
          .run(Either.monadError()) == Right(Some(f(s)))
      }
    }

    "asHandler"{
      forAll(Gen.string(), genThrowable()) { s, t ->
        FreeC.pure<EitherPartialOf<Throwable>, String>(s)
          .asHandler(t)
          .run(Either.monadError()) == Left(t)
      }
    }

    "translate pure value"{
      forAll(Gen.string()) { s ->
        FreeC.pure<EitherPartialOf<Throwable>, String>(s)
          .translate(EitherToTry)
          .run(Try.monadError()) == Success(Some(s))
      }
    }

    "translate fail value"{
      forAll(genThrowable()) { t ->
        FreeC.raiseError<EitherPartialOf<Throwable>, String>(t)
          .translate(EitherToTry)
          .run(Try.monadError()) == Failure(t)
      }
    }

    "Running a interrupted value without errors" {
      FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), None)
        .translate(EitherToTry)
        .run(Try.monadError()) == Success(None)
    }

    "Running a interrupted value with errors" {
      forAll(genThrowable()) { t ->
        FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), t.some())
          .translate(EitherToTry)
          .run(Try.monadError()) == Failure(t)
      }
    }

    "translate liftF value"{
      forAll(Gen.string()) { s ->
        FreeC.liftF<EitherPartialOf<Throwable>, String>(s.right())
          .translate(EitherToTry)
          .run(Try.monadError()) == Success(Some(s))
      }
    }

    "Running a Bind value" {
      forAll(Gen.string()) { s ->
        FreeC.Bind(FreeC.pure("")) { _ -> FreeC.pure<EitherPartialOf<Throwable>, String>(s) }
          .translate(EitherToTry)
          .run(Try.monadError()) == Success(Some(s))
      }
    }

  }
}

internal object EitherToTry : FunctionK<EitherPartialOf<Throwable>, ForTry> {
  override fun <A> invoke(fa: Kind<EitherPartialOf<Throwable>, A>): Kind<ForTry, A> =
    fa.fix().fold({ Try.Failure(it) }, { Try.Success(it) })
}