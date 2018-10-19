package arrow.data

import arrow.core.*
import arrow.instances.*
import arrow.instances.either.monadError.monadError
import arrow.instances.id.monad.monad
import arrow.instances.id.traverse.traverse
import arrow.instances.option.functor.functor
import arrow.test.UnitSpec
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {
  init {

      testLaws(
        MonadErrorLaws.laws(Either.monadError(), Eq.any(), Eq.any()),
        TraverseLaws.laws(EitherT.traverse<ForId, Int>(Id.traverse()), EitherT.applicative<ForId, Int>(Id.monad()), { EitherT(Id(Right(it))) }, Eq.any()),
        SemigroupKLaws.laws<EitherTPartialOf<ForId, Int>>(
          EitherT.semigroupK(Id.monad()),
          EitherT.applicative(Id.monad()),
          Eq.any())
      )

    "mapLeft should alter left instance only" {
      forAll { i: Int, j: Int ->
        val left: Either<Int, Int> = Left(i)
        val right: Either<Int, Int> = Right(j)
        EitherT(Option(left)).mapLeft(Option.functor()) { it + 1 } == EitherT(Option(Left(i + 1))) &&
          EitherT(Option(right)).mapLeft(Option.functor()) { it + 1 } == EitherT(Option(right)) &&
          EitherT(Option.empty<Either<Int, Int>>()).mapLeft(Option.functor()) { it + 1 } == EitherT(Option.empty<Either<Int, Int>>())
      }
    }

  }
}
