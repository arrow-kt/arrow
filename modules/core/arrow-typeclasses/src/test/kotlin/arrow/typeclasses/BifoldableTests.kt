package arrow.typeclasses

import arrow.Kind2
import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.BifoldableLaws
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
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

    testLaws(BifoldableLaws.laws(eitherComposeEither, { cf: Int -> Right(Right(cf)).binest() }, Eq.any()))
  }
}
