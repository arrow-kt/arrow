package arrow.streams.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.MonadDefer
import arrow.instances.eq
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genOption
import arrow.test.generators.genThrowable
import arrow.test.laws.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Eq
import arrow.typeclasses.MonadError
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.forAll
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.properties.map
import org.junit.runner.RunWith
import java.lang.RuntimeException

@RunWith(KTestJUnitRunner::class)
class FreeCTest : UnitSpec() {

  init {

    testLaws(FunctorLaws.laws(
      FF = FreeC.functor<ForTry>(),
      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
      f = { FreeC.pure(it) }
    ))

//    testLaws(ApplicativeLaws.laws(
//      A = object : FreeCApplicative<ForTry> {},
//      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
//    ))
//
//    testLaws(MonadLaws.laws(
//      M = object : FreeCMonad<ForTry> {},
//      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
//    ))
//
//    testLaws(MonadErrorLaws.laws(
//      M = object : FreeCMonadError<ForTry> {},
//      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
//      EQERR = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
//      EQ_EITHER = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
//    ))
//
//    testLaws(ApplicativeErrorLaws.laws(
//      AE = object : FreeCApplicativeError<ForTry> {},
//      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
//      EQERR = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
//      EQ_EITHER = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
//    ))
//
//    testLaws(MonadDeferLaws.laws(
//      SC = object : FreeCMonadDefer<ForTry> { },
//      EQ = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
//      EQERR = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) },
//      EQ_EITHER = Eq { a, b -> a.run(Try.monadError()) == b.run(Try.monadError()) }
//    ))

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
        FreeC.eval(s.right())
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
        FreeC.suspend() { FreeC.pure<EitherPartialOf<Throwable>, String>(s) }
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

    "translate eval value"{
      forAll(Gen.string()) { s ->
        FreeC.eval<EitherPartialOf<Throwable>, String>(s.right())
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

object EitherToTry : FunctionK<EitherPartialOf<Throwable>, ForTry> {
  override fun <A> invoke(fa: Kind<EitherPartialOf<Throwable>, A>): Kind<ForTry, A> =
    fa.fix().fold({ Try.Failure(it) }, { Try.Success(it) })
}