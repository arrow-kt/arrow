package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function0Test : UnitSpec() {
    val EQ: Eq<HK<Function0HK, Int>> = object : Eq<HK<Function0HK, Int>> {
        override fun eqv(a: HK<Function0HK, Int>, b: HK<Function0HK, Int>): Boolean =
                a() == b()
    }

    init {
        testLaws(MonadLaws.laws(Function0.monad(), EQ))
        testLaws(ComonadLaws.laws(Function0.comonad(), { { it }.k() }, EQ))
    }
}
