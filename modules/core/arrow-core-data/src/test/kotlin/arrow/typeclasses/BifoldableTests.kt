package arrow.typeclasses

import arrow.Kind2
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.fix
import arrow.mtl.typeclasses.binest
import arrow.mtl.typeclasses.compose
import arrow.test.UnitSpec
import arrow.test.generators.either
import arrow.test.generators.intSmall
import arrow.test.laws.BifoldableLaws
import io.kotlintest.properties.Gen

class BifoldableTests : UnitSpec() {
  init {
    val eitherBifoldable: Bifoldable<ForEither> = object : Bifoldable<ForEither> {
      override fun <A, B, C> Kind2<ForEither, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
        this.fix().run {
          when (this) {
            is Either.Left -> f(c, a)
            is Either.Right -> g(c, b)
          }
        }

      override fun <A, B, C> Kind2<ForEither, A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
        when (this) {
          is Either.Left -> f(a, c)
          else -> g((this as Either.Right).b, c)
        }
    }

    val eitherComposeEither = eitherBifoldable.compose(eitherBifoldable)

    val eitherGen = Gen.either(Gen.intSmall(), Gen.intSmall())
    val nestedEitherGen = Gen.either(eitherGen, eitherGen).map { it.binest() }

    testLaws(BifoldableLaws.laws(eitherComposeEither, nestedEitherGen, Eq.any()))
  }
}
