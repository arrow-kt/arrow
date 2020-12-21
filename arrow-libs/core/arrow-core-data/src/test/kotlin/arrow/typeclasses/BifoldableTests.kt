package arrow.typeclasses

import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.ForEither
import arrow.core.extensions.either.bifoldable.bifoldable
import arrow.core.test.UnitSpec
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.either
import arrow.core.test.laws.BifoldableLaws
import io.kotlintest.properties.Gen

class BifoldableTests : UnitSpec() {
  init {
    val genK2 = object : GenK2<ForEither> {
      override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<EitherOf<A, B>> =
        Gen.either(genA, genB) as Gen<EitherOf<A, B>>
    }

    testLaws(BifoldableLaws.laws(Either.bifoldable(), genK2))
  }
}
