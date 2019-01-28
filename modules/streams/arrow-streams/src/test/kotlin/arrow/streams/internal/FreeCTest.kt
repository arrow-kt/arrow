package arrow.streams.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.extensions.io.monadError.monadError
import arrow.higherkind
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.either.monadError.monadError
import arrow.streams.internal.freec.eq.eq
import arrow.streams.internal.freec.monadDefer.monadDefer
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genThrowable
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadDeferLaws
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@higherkind
sealed class Ops<out A> : OpsOf<A> {

  data class Value(val a: Int) : Ops<Int>()
  data class Add(val a: Int, val y: Int) : Ops<Int>()
  data class Subtract(val a: Int, val y: Int) : Ops<Int>()

  companion object : FreeCMonadDefer<ForOps> {
    fun value(n: Int): FreeC<ForOps, Int> = FreeC.liftF(Value(n))
    fun add(n: Int, y: Int): FreeC<ForOps, Int> = FreeC.liftF(Add(n, y))
    fun subtract(n: Int, y: Int): FreeC<ForOps, Int> = FreeC.liftF(Subtract(n, y))
  }
}

@Suppress("UNCHECKED_CAST")
val eitherInterpreter: FunctionK<ForOps, EitherPartialOf<Throwable>> = object : FunctionK<ForOps, EitherPartialOf<Throwable>> {
  override fun <A> invoke(fa: Kind<ForOps, A>): Either<Throwable, A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> Right(op.a + op.y)
      is Ops.Subtract -> Right(op.a - op.y)
      is Ops.Value -> Right(op.a)
    } as Either<Throwable, A>
  }
}

@Suppress("UNCHECKED_CAST")
val ioInterpreter: FunctionK<ForOps, ForIO> = object : FunctionK<ForOps, ForIO> {
  override fun <A> invoke(fa: Kind<ForOps, A>): IO<A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> IO { op.a + op.y }
      is Ops.Subtract -> IO { op.a - op.y }
      is Ops.Value -> IO { op.a }
    } as IO<A>
  }
}

private val program = Ops.fx {
  val (added) = Ops.add(10, 10)
  val subtracted = bind { Ops.subtract(added, 50) }
  subtracted
}.fix()

private fun stackSafeTestProgram(n: Int, stopAt: Int): FreeC<ForOps, Int> = Ops.fx {
  val (v) = Ops.add(n, 1)
  val r = bind { if (v < stopAt) stackSafeTestProgram(v, stopAt) else FreeC.just(v) }
  r
}.fix()

@RunWith(KotlinTestRunner::class)
class FreeCTest : UnitSpec() {

  init {

    val EQ: Eq<Kind<FreeCPartialOf<ForOps>, Int>> = FreeC.eq(Either.monadError(), eitherInterpreter, Eq.any())

    testLaws(
      EqLaws.laws(EQ) { Ops.value(it) },
      MonadDeferLaws.laws(
        SC = Ops,
        EQ = FreeC.eq(Either.monadError(), eitherInterpreter, Eq.any()),
        EQ_EITHER = FreeC.eq(Either.monadError(), eitherInterpreter, Eq.any()),
        EQERR = FreeC.eq(Either.monadError(), eitherInterpreter, Eq.any())
      )
    )
    testLaws(
      MonadDeferLaws.laws(
        SC = FreeC.monadDefer(),
        EQ = FreeC.eq(Try.monadError(), FunctionK.id(), Eq.any()),
        EQ_EITHER = FreeC.eq(Try.monadError(), FunctionK.id(), Eq.any()),
        EQERR = FreeC.eq(Try.monadError(), FunctionK.id(), Eq.any())
      ))

    "Can interpret an ADT as Free operations" {
      program.foldMap(eitherInterpreter, Either.monadError()).fix() shouldBe Right(Some(-30))
      program.foldMap(ioInterpreter, IO.monadError()).fix().unsafeRunSync() shouldBe Some(-30)
    }

    "foldMap is stack safe" {
      val n = 5000
      val hugeProg = stackSafeTestProgram(0, n)
      hugeProg
        .foldMap(ioInterpreter, IO.monadError())
        .fix()
        .unsafeRunSync() shouldBe Some(n)

      hugeProg.foldMap(eitherInterpreter, Either.monadError()).fix() shouldBe Right(Some(n))
    }

    "errors are correctly captured" {
      forAll(Gen.string(), genThrowable()) { s, t ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .map(::identity)
          .flatMap<String> { throw  t }
          .run(Either.monadError()) == Left(t)
      }
    }

    "Running a just value" {
      forAll(Gen.string()) { s ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "Running an error value" {
      forAll(genThrowable()) { t ->
        FreeC.raiseError<EitherPartialOf<Throwable>, Int>(t)
          .run(Either.monadError()) == Left(t)
      }
    }

    "Running an Suspend value" {
      forAll(Gen.string()) { s ->
        FreeC.liftF<EitherPartialOf<Throwable>, String>(s.right())
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "Running a deferred value"{
      forAll(Gen.string()) { s ->
        FreeC.defer { FreeC.just<EitherPartialOf<Throwable>, String>(s) }
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "map" {
      forAll(Gen.string(), genFunctionAToB<String, String>(Gen.string())) { s, f ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .map(f)
          .run(Either.monadError()) == Right(Some(f(s)))
      }
    }

    "flatMap"{
      forAll(Gen.string(), genFunctionAToB<String, String>(Gen.string())) { s, f ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .flatMap { FreeC.just<EitherPartialOf<Throwable>, String>(f(it)) }
          .run(Either.monadError()) == Right(Some(f(s)))
      }
    }

    "asHandler"{
      forAll(Gen.string(), genThrowable()) { s, t ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .asHandler(t)
          .run(Either.monadError()) == Left(t)
      }
    }

    "translate just value"{
      forAll(Gen.string()) { s ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .foldMap(EitherToTry, Try.monadError()) == Success(Some(s))
      }
    }

    "translate fail value"{
      forAll(genThrowable()) { t ->
        FreeC.raiseError<EitherPartialOf<Throwable>, String>(t)
          .foldMap(EitherToTry, Try.monadError()) == Failure(t)
      }
    }

    "Running a interrupted value without errors using Either" {
      FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), None)
        .run(Either.monadError()) shouldBe Right(None)
    }

    "Running a interrupted value without errors using Try" {
      FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), None)
        .foldMap(EitherToTry, Try.monadError()) shouldBe Success(None)
    }

    "Running a interrupted value with errors using Either" {
      forAll(genThrowable()) { t ->
        FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), t.some())
          .run(Either.monadError()) == Left(t)
      }
    }

    "Running a interrupted value with errors using Try" {
      forAll(genThrowable()) { t ->
        FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), t.some())
          .foldMap(EitherToTry, Try.monadError()) == Failure(t)
      }
    }

    "translate liftF value"{
      forAll(Gen.string()) { s ->
        FreeC.liftF<EitherPartialOf<Throwable>, String>(s.right())
          .foldMap(EitherToTry, Try.monadError()) == Success(Some(s))
      }
    }

    "Running a FlatMapped value using Either" {
      forAll(Gen.string()) { s ->
        FreeC.FlatMapped(FreeC.just("")) { FreeC.just<EitherPartialOf<Throwable>, String>(s) }
          .run(Either.monadError()) == Right(Some(s))

      }
    }

    "Running a FlatMapped value using Try" {
      forAll(Gen.string()) { s ->
        FreeC.FlatMapped(FreeC.just("")) { FreeC.just<EitherPartialOf<Throwable>, String>(s) }
          .foldMap(EitherToTry, Try.monadError()) == Success(Some(s))
      }
    }

  }
}

internal object EitherToTry : FunctionK<EitherPartialOf<Throwable>, ForTry> {
  override fun <A> invoke(fa: Kind<EitherPartialOf<Throwable>, A>): Kind<ForTry, A> =
    fa.fix().fold({ Try.Failure(it) }, { Try.Success(it) })
}