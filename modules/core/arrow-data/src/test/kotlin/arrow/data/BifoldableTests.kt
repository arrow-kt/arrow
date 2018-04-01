package arrow.data

import arrow.Kind2
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.Right
import arrow.test.UnitSpec
import arrow.test.laws.BifoldableLaws
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Eq
import arrow.typeclasses.binest
import arrow.typeclasses.compose
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class BifoldableTests : UnitSpec() {
  init {

    val eitherBifoldable: Bifoldable<ForEither> = object : Bifoldable<ForEither> {
      override fun <A, B, C> arrow.Kind2<arrow.core.ForEither, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
        when (this@bifoldLeft) {
          is Either.Left -> f(c, this@bifoldLeft.a)
          else -> g(c, (this@bifoldLeft as Either.Right).b)
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
