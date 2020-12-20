package arrow.typeclasses

import arrow.Kind2
import arrow.core.Either
import arrow.core.ForEither
import arrow.core.extensions.either.bifoldable.bifoldable
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.binest
import arrow.mtl.typeclasses.compose
import arrow.core.test.UnitSpec
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.either
import arrow.core.test.generators.intSmall
import arrow.core.test.laws.BifoldableLaws
import io.kotlintest.properties.Gen

class BifoldableTests : UnitSpec() {
  init {
    val eitherComposeEither = Either.bifoldable().compose(Either.bifoldable())

    val eitherGen = Gen.either(Gen.intSmall(), Gen.intSmall())

    val genK2 = object : GenK2<Nested<ForEither, ForEither>> {
      override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<Nested<ForEither, ForEither>, A, B>> =
        Gen.either(eitherGen, Gen.either(genA, genB)).map {
          it.binest()
        } as Gen<Kind2<Nested<ForEither, ForEither>, A, B>>
    }

    testLaws(BifoldableLaws.laws(eitherComposeEither, genK2))
  }
}
