package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.instances.applicative
import arrow.instances.const.divisible.divisible
import arrow.instances.either.monadError.monadError
import arrow.instances.eithert.divisible.divisible
import arrow.instances.function1.divisible.divisible
import arrow.instances.id.monad.monad
import arrow.instances.id.traverse.traverse
import arrow.instances.monoid
import arrow.instances.option.functor.functor
import arrow.instances.semigroupK
import arrow.instances.traverse
import arrow.test.UnitSpec
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTTest : UnitSpec() {

  init {
    testLaws(
      DivisibleLaws.laws(
        EitherT.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        { i -> EitherT(Const(i)) },
        Eq { a: Kind<EitherTPartialOf<ConstPartialOf<Int>, Int>, Int>, b ->
          a.fix().value.fix().value == b.fix().value.fix().value
        }
      ),
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
