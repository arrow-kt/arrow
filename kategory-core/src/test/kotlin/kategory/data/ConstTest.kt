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
            semigroup<ConstKind<Int, Int>>() shouldNotBe null
            monoid<ConstKind<Int, Int>>() shouldNotBe null
        }

        testLaws(TraverseLaws.laws(Const.traverse(), Const.applicative(IntMonoid), { Const(it) }, Eq.any()))
        testLaws(ApplicativeLaws.laws(Const.applicative(IntMonoid), Eq.any()))
    }
}

fun <A> List<A>?.isNotEmpty(): Boolean =
        this != null && this.size > 0

object test {
    val x : List<Int>? = null
    val xIsNotEmpty: Boolean = x.isNotEmpty()
    //false
    val y : List<Int>? = emptyList<Int>()
    val yIsNotEmpty: Boolean = y.isNotEmpty()
    //false
    val z : List<Int>? = listOf(1)
    val zIsNotEmpty: Boolean = y.isNotEmpty()
    //true
}

