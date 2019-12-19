package arrow.streams.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Failure
import arrow.core.ForTry
import arrow.core.FunctionK
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Success
import arrow.core.Try
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.either.monadError.monadError
import arrow.core.extensions.option.eq.eq
import arrow.core.fix
import arrow.core.identity
import arrow.core.right
import arrow.core.some
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.monadError.monadError
import arrow.fx.fix
import arrow.higherkind
import arrow.streams.internal.freec.applicative.applicative
import arrow.streams.internal.freec.eq.eq
import arrow.streams.internal.freec.functor.functor
import arrow.streams.internal.freec.monad.monad
import arrow.streams.internal.freec.monadDefer.monadDefer
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.throwable
import arrow.test.laws.EqLaws
import arrow.test.laws.MonadDeferLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

@higherkind
sealed class Ops<out A> : OpsOf<A> {

  data class Value<A>(val a: Int, val k: (Int) -> A) : Ops<A>()
  data class Add<A>(val a: Int, val y: Int, val k: (Int) -> A) : Ops<A>()
  data class Subtract<A>(val a: Int, val y: Int, val k: (Int) -> A) : Ops<A>()

  companion object : FreeCMonadDefer<ForOps> {
    fun value(n: Int): FreeC<ForOps, Int> = FreeC.liftF(Value(n, ::identity))
    fun add(n: Int, y: Int): FreeC<ForOps, Int> = FreeC.liftF(Add(n, y, ::identity))
    fun subtract(n: Int, y: Int): FreeC<ForOps, Int> = FreeC.liftF(Subtract(n, y, ::identity))
  }
}

private fun genOps() = Gen.int().map { Ops.value(it) }

@Suppress("UNCHECKED_CAST")
val eitherInterpreter: FunctionK<ForOps, EitherPartialOf<Throwable>> = object : FunctionK<ForOps, EitherPartialOf<Throwable>> {
  override fun <A> invoke(fa: Kind<ForOps, A>): Either<Throwable, A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> Right(op.k(op.a + op.y))
      is Ops.Subtract -> Right(op.k(op.a - op.y))
      is Ops.Value -> Right(op.k(op.a))
    }
  }
}

@Suppress("UNCHECKED_CAST")
val ioInterpreter: FunctionK<ForOps, ForIO> = object : FunctionK<ForOps, ForIO> {
  override fun <A> invoke(fa: Kind<ForOps, A>): IO<Nothing, A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> IO { op.k(op.a + op.y) }
      is Ops.Subtract -> IO { op.k(op.a - op.y) }
      is Ops.Value -> IO { op.k(op.a) }
    } as IO<Nothing, A>
  }
}

private val program = Ops.fx.monad {
  val (added) = Ops.add(10, 10)
  val subtracted = !Ops.subtract(added, 50)
  subtracted
}.fix()

private fun stackSafeTestProgram(n: Int, stopAt: Int): FreeC<ForOps, Int> = Ops.fx.monad {
  val (v) = Ops.add(n, 1)
  val r = !if (v < stopAt) stackSafeTestProgram(v, stopAt) else FreeC.just(v)
  r
}.fix()

class FreeCTest : UnitSpec() {

  init {

    val EQ: Eq<Kind<FreeCPartialOf<ForOps>, Int>> = FreeC.eq(Either.monadError(), eitherInterpreter, Eq.any())

    val EQK = object : EqK<FreeCPartialOf<ForOps>> {
      override fun <A> Kind<FreeCPartialOf<ForOps>, A>.eqK(other: Kind<FreeCPartialOf<ForOps>, A>, EQ: Eq<A>): Boolean =
        (this.fix() to other.fix()).let { (ls, rs) ->
          val eq1 = Either.eq(Eq.any(), Option.eq(EQ)) as Eq<Kind<EitherPartialOf<Throwable>, Option<A>>>
          val eq2 = FreeC.eq<ForOps, EitherPartialOf<Throwable>, A>(Either.monadError(), eitherInterpreter, eq1)

          eq2.run {
            ls.eqv(rs)
          }
        }
    }

    val opsGENK = object : GenK<FreeCPartialOf<ForOps>> {
      override fun <A> genK(gen: Gen<A>): Gen<Kind<FreeCPartialOf<ForOps>, A>> =
        Gen.bind(Gen.int(), gen) { i, r ->
          FreeC.liftF(Ops.Value(i) { r })
        }
    }

    testLaws(
      EqLaws.laws(EQ, genOps()),
      MonadDeferLaws.laws(
        Ops,
        opsGENK,
        EQK
      )
    )
    testLaws(
      MonadDeferLaws.laws(
        SC = FreeC.monadDefer(),
        FF = FreeC.functor(),
        AP = FreeC.applicative(),
        SL = FreeC.monad(),
        GENK = opsGENK,
        EQK = EQK

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
      forAll(Gen.string(), Gen.throwable()) { s, t ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .map(::identity)
          .flatMap<String> { throw t }
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
      forAll(Gen.throwable()) { t ->
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

    "Running a deferred value" {
      forAll(Gen.string()) { s ->
        FreeC.defer { FreeC.just<EitherPartialOf<Throwable>, String>(s) }
          .run(Either.monadError()) == Right(Some(s))
      }
    }

    "map" {
      forAll(Gen.string(), Gen.functionAToB<String, String>(Gen.string())) { s, f ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .map(f)
          .run(Either.monadError()) == Right(Some(f(s)))
      }
    }

    "flatMap" {
      forAll(Gen.string(), Gen.functionAToB<String, String>(Gen.string())) { s, f ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .flatMap { FreeC.just<EitherPartialOf<Throwable>, String>(f(it)) }
          .run(Either.monadError()) == Right(Some(f(s)))
      }
    }

    "asHandler" {
      forAll(Gen.string(), Gen.throwable()) { s, t ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .asHandler(t)
          .run(Either.monadError()) == Left(t)
      }
    }

    "translate just value" {
      forAll(Gen.string()) { s ->
        FreeC.just<EitherPartialOf<Throwable>, String>(s)
          .foldMap(EitherToTry, Try.monadError()) == Success(Some(s))
      }
    }

    "translate fail value" {
      forAll(Gen.throwable()) { t ->
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
      forAll(Gen.throwable()) { t ->
        FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), t.some())
          .run(Either.monadError()) == Left(t)
      }
    }

    "Running a interrupted value with errors using Try" {
      forAll(Gen.throwable()) { t ->
        FreeC.interrupted<EitherPartialOf<Throwable>, String, Token>(Token(), t.some())
          .foldMap(EitherToTry, Try.monadError()) == Failure(t)
      }
    }

    "translate liftF value" {
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
