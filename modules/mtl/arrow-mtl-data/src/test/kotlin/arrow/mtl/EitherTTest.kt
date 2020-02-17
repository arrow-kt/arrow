package arrow.mtl

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Either
import arrow.core.ForConst
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.core.extensions.const.divisible.divisible
import arrow.core.extensions.const.eqK.eqK
import arrow.core.extensions.eq
import arrow.core.extensions.id.eqK.eqK
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.id.traverse.traverse
import arrow.core.extensions.monoid
import arrow.core.extensions.option.functor.functor
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.functor
import arrow.fx.extensions.io.monad.monad
import arrow.fx.mtl.concurrent
import arrow.fx.mtl.timer
import arrow.mtl.extensions.eithert.alternative.alternative
import arrow.mtl.extensions.eithert.applicative.applicative
import arrow.mtl.extensions.eithert.divisible.divisible
import arrow.mtl.extensions.eithert.eqK.eqK
import arrow.mtl.extensions.eithert.functor.functor
import arrow.mtl.extensions.eithert.monad.monad
import arrow.mtl.extensions.eithert.semigroupK.semigroupK
import arrow.mtl.extensions.eithert.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.laws.AlternativeLaws
import arrow.test.laws.ConcurrentLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class EitherTTest : UnitSpec() {

  init {
    val idEQK: EqK<Kind<Kind<ForEitherT, ForId>, Int>> = EitherT.eqK(Id.eqK(), Int.eq())

    val ioEQK: EqK<Kind<Kind<ForEitherT, ForIO>, String>> = EitherT.eqK(IO.eqK(), Eq.any())

    val constEQK: EqK<Kind<Kind<ForEitherT, Kind<ForConst, Int>>, Int>> = EitherT.eqK(Const.eqK(Int.eq()), Int.eq())

    testLaws(
      DivisibleLaws.laws(
        EitherT.divisible<ConstPartialOf<Int>, Int>(Const.divisible(Int.monoid())),
        EitherT.genK(Const.genK(Gen.int()), Gen.int()),
        constEQK
      ),

      AlternativeLaws.laws(
        EitherT.alternative(Id.monad(), Int.monoid()),
        EitherT.genK(Id.genK(), Gen.int()),
        idEQK
      ),

      ConcurrentLaws.laws<EitherTPartialOf<ForIO, String>>(
        EitherT.concurrent(IO.concurrent()),
        EitherT.timer<ForIO, String>(IO.concurrent()),
        EitherT.functor(IO.functor()),
        EitherT.applicative(IO.applicative()),
        EitherT.monad(IO.monad()),
        EitherT.genK(IO.genK(), Gen.string()),
        ioEQK
      ),

      TraverseLaws.laws(EitherT.traverse<ForId, Int>(Id.traverse()),
        EitherT.genK(Id.genK(), Gen.int()),
        idEQK),

      SemigroupKLaws.laws(
        EitherT.semigroupK<ForId, Int>(Id.monad()),
        EitherT.genK(Id.genK(), Gen.int()),
        idEQK
      )
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
