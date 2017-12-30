package arrow.data

import arrow.HK2
import arrow.core.*
import arrow.instances.*
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.BifoldableLaws
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Eq
import arrow.typeclasses.binest

@RunWith(KTestJUnitRunner::class)
class BifoldableTests : UnitSpec() {
    init {

        val eitherBifoldable: Bifoldable<EitherHK> = object : Bifoldable<EitherHK> {
            override fun <A, B, C> bifoldLeft(fab: HK2<EitherHK, A, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C =
                    when (fab) {
                        is Either.Left -> f(c, fab.a)
                        else -> g(c, (fab as Either.Right).b)
                    }

            override fun <A, B, C> bifoldRight(fab: HK2<EitherHK, A, B>, c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
                    when (fab) {
                        is Either.Left -> f(fab.a, c)
                        else -> g((fab as Either.Right).b, c)
                    }
        }

        val eitherComposeEither = eitherBifoldable.compose(eitherBifoldable)

        testLaws(BifoldableLaws.laws(eitherComposeEither, { cf: Int -> Right(Right(cf)).binest() }, Eq.any()))
    }
}
