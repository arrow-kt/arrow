package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ConstTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            functor<ConstKindPartial<Int>>() shouldNotBe null
            applicative<ConstKindPartial<Int>>() shouldNotBe null
            foldable<ConstKindPartial<Int>>() shouldNotBe null
            traverse<ConstKindPartial<Int>>() shouldNotBe null
            traverseFilter<ConstKindPartial<Int>>() shouldNotBe null
            semigroup<ConstKind<Int, Int>>() shouldNotBe null
            monoid<ConstKind<Int, Int>>() shouldNotBe null
        }

        testLaws(TraverseFilterLaws.laws(Const.traverseFilter(IntMonoid), Const.applicative(IntMonoid), { Const(it) }, Eq.any()))
        testLaws(ApplicativeLaws.laws(Const.applicative(IntMonoid), Eq.any()))
    }
}
