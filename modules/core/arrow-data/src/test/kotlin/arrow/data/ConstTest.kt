package arrow.data

import arrow.instances.IntMonoid
import arrow.mtl.traverseFilter
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<ConstPartialOf<Int>>() shouldNotBe null
            applicative<ConstPartialOf<Int>>() shouldNotBe null
            foldable<ConstPartialOf<Int>>() shouldNotBe null
            traverse<ConstPartialOf<Int>>() shouldNotBe null
            traverseFilter<ConstPartialOf<Int>>() shouldNotBe null
            semigroup<ConstOf<Int, Int>>() shouldNotBe null
            monoid<ConstOf<Int, Int>>() shouldNotBe null
            eq<Const<Int, String>>() shouldNotBe null
        }

        testLaws(
            TraverseFilterLaws.laws(Const.traverseFilter(), Const.applicative(IntMonoid), { Const(it) }, Eq.any()),
            ApplicativeLaws.laws(Const.applicative(IntMonoid), Eq.any()),
            EqLaws.laws(eq<Const<Int, String>>(), { Const(it) })
        )
    }
}
